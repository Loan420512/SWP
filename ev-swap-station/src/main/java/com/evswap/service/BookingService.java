package com.evswap.service;

import com.evswap.dto.BookingResponse;
import com.evswap.dto.MomoQRResponse;
import com.evswap.entity.*;
import com.evswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final InventoryRepository inventoryRepo;
    private final TransactionRepository txnRepo;
    private final UserRepository userRepo;
    private final StationRepository stationRepo;
    private final VehicleRepository vehicleRepo;
    private final BatteryRepository batteryRepo;
    private final TransactionRepository transactionRepo;

    /**
     * Thực hiện BR1 – BOOKED (cọc 20%). Trả về DTO đã “dựng sẵn” dữ liệu để không dính lazy proxy.
     */
    @Transactional
    public BookingResponse bookWithPassiveDeposit(Integer userId,
                                                  Integer stationId,
                                                  Integer vehicleId,
                                                  Integer batteryId,
                                                  OffsetDateTime timeSlotOffset,
                                                  BigDecimal estimatedPrice,   // có thể null/<=0
                                                  int holdMinutes) {

        // ---- validate inputs ----
        if (timeSlotOffset == null)
            throw new IllegalArgumentException("timeSlot required");

        LocalDateTime timeSlot = timeSlotOffset.toLocalDateTime();
        if (!timeSlot.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("timeSlot must be in the future");

        if (holdMinutes <= 0) holdMinutes = 30;

        // ---- load basic entities ----
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!"Active".equalsIgnoreCase(nz(user.getStatus())))
            throw new IllegalStateException("User is not active");

        Station station = stationRepo.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));
        // DB đang dùng CHECK (Open/Closed), nên coi "Open" là active
        if (!"Open".equalsIgnoreCase(nz(station.getStationStatus())))
            throw new IllegalStateException("Station is not active");

        Vehicle vehicle = null;
        if (vehicleId != null) {
            vehicle = vehicleRepo.findByIdAndUser(vehicleId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found or not owned by user"));
        }

        Battery battery = batteryRepo.findById(batteryId)
                .orElseThrow(() -> new IllegalArgumentException("Battery not found"));

        // Trạng thái pin hợp lệ cho kinh doanh (theo CHECK hiện tại: Full/Empty/Maintenance/Damaged)
        if (!"Full".equalsIgnoreCase(nz(battery.getStatus())))
            throw new IllegalStateException("Battery is not sellable (must be Full)");

        // ---- tính giá nếu client bỏ trống/<=0 ----
        BigDecimal batteryPrice = toBig(battery.getPrice()); // battery.getPrice() có thể là Double trong entity
        if (estimatedPrice == null || estimatedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            if (batteryPrice == null || batteryPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Price for this battery is not configured");
            }
            estimatedPrice = batteryPrice;
        }

        // ---- chặn trùng lịch gần nhau ----
        long overlap = bookingRepo.countOpenAround(userId, timeSlot, 30);
        if (overlap > 0) throw new IllegalStateException("You already have a booking around that time");

        // ---- giữ (hold) hàng tồn kho ----
        Inventory inv = inventoryRepo.lockForUpdate(stationId, batteryId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));
        int available = nz(inv.getReadyQty()) - nz(inv.getHoldQty());
        if (available <= 0) throw new IllegalStateException("Out of stock for this station/battery");
        inv.setHoldQty(nz(inv.getHoldQty()) + 1);
        inventoryRepo.save(inv);

        // ---- tạo booking + giao dịch cọc 20% ----
        BigDecimal deposit = estimatedPrice.multiply(new BigDecimal("0.20"))
                .setScale(0, RoundingMode.HALF_UP);

        Booking booking = Booking.builder()
                .user(User.builder().id(userId).build())
                .station(Station.builder().id(stationId).build())
                .vehicle(vehicleId != null ? Vehicle.builder().id(vehicleId).build() : null)
                .battery(Battery.builder().id(batteryId).build())
                .timeDate(timeSlot)
                .estimatedPrice(estimatedPrice)
                .depositAmount(deposit)
                .depositStatus("PENDING")
                .status("BOOKED")
                .holdUntil(LocalDateTime.now().plusMinutes(holdMinutes))
                .build();
        booking = bookingRepo.save(booking);

        Transaction tx = Transaction.builder()
                .user(User.builder().id(userId).build())
                .station(Station.builder().id(stationId).build())
                .booking(booking)
                .amount(deposit)
                .transactionType("DEPOSIT")
                .status("PENDING")
                .transactionTime(LocalDateTime.now())
                .build();
        txnRepo.save(tx);

        // ---- dựng DTO trả về (đủ thông tin, không lazy) ----
        return BookingResponse.builder()
                .id(booking.getId())
                .user(BookingResponse.UserInfo.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .build())
                .station(BookingResponse.StationInfo.builder()
                        .id(station.getId())
                        .name(station.getStationName())
                        .address(station.getAddress())
                        .status(station.getStationStatus())
                        .build())
                .vehicle(vehicle == null ? null :
                        BookingResponse.VehicleInfo.builder()
                                .id(vehicle.getId())
                                .vin(vehicle.getVin())
                                .model(vehicle.getVehicleModel())
                                .batteryType(vehicle.getBatteryType())
                                .build())
                .battery(BookingResponse.BatteryInfo.builder()
                        .id(battery.getId())
                        .name(battery.getBatteryName())
                        .price(batteryPrice)            // CHÚ Ý: BigDecimal
                        .status(battery.getStatus())
                        .build())
                .timeDate(booking.getTimeDate())
                .estimatedPrice(booking.getEstimatedPrice())
                .depositAmount(booking.getDepositAmount())
                .depositStatus(booking.getDepositStatus())
                .status(booking.getStatus())
                .holdUntil(booking.getHoldUntil())
                .cancelReason(booking.getCancelReason())
                .canceledAt(booking.getCanceledAt())
                .build();
    }

    // -------- helpers ----------
    private static int nz(Integer v) { return v == null ? 0 : v; }

    private static String nz(String s) { return s == null ? "" : s; }

    /**
     * Chuyển Double (từ entity cũ) sang BigDecimal an toàn. Nếu null → null.
     */
    private static BigDecimal toBig(Double d) {
        return d == null ? null : BigDecimal.valueOf(d);
    }

    /**
     * Xác nhận thanh toán đặt cọc thành công.
     */
    @Transactional
    public BookingResponse confirmDeposit(Long id, String txnRef) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!"BOOKED".equalsIgnoreCase(booking.getStatus()))
            throw new IllegalStateException("Booking not eligible for deposit confirmation");

        booking.setDepositStatus("PAID");
        bookingRepo.save(booking);

        // Cập nhật Transaction tương ứng
        Transaction tx = txnRepo.findFirstByBookingIdAndTransactionTypeOrderByTransactionTimeDesc(
                booking.getId(), "DEPOSIT"
        ).orElse(null);
        if (tx != null) {
            tx.setStatus("SUCCESS");
            tx.setTransactionRef(txnRef);
            txnRepo.save(tx);
        }

        return toResponse(booking);
    }

    /**
     * Tự động hủy booking quá hạn (chưa đến trạm, quá holdUntil).
     * Chạy mỗi 5 phút.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void autoCancelExpiredBookings() {
        var expired = bookingRepo.findAllByStatusAndHoldUntilBefore("BOOKED", LocalDateTime.now());
        for (Booking b : expired) {
            b.setStatus("CANCELLED");
            b.setCancelReason("Auto-cancelled due to timeout");
            b.setCanceledAt(LocalDateTime.now());
            bookingRepo.save(b);

            Inventory inv = inventoryRepo.findByStationIdAndBatteryId(
                    b.getStation().getId(), b.getBattery().getId()
            ).orElse(null);
            if (inv != null && nz(inv.getHoldQty()) > 0) {
                inv.setHoldQty(inv.getHoldQty() - 1);
                inventoryRepo.save(inv);
            }
        }
    }

    /**
     * Hủy booking với lý do cụ thể.
     */
//    @Transactional
//    public BookingResponse cancelBooking(Long id, String reason) {
//        Booking booking = bookingRepo.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
//
//        if (!"BOOKED".equalsIgnoreCase(booking.getStatus()) &&
//                !"ARRIVED".equalsIgnoreCase(booking.getStatus())) {
//            throw new IllegalStateException("Booking cannot be cancelled at this stage");
//        }
//
//        booking.setStatus("CANCELLED");
//        booking.setCancelReason(reason);
//        booking.setCanceledAt(LocalDateTime.now());
//        bookingRepo.save(booking);
//
//        // Giải phóng hàng tồn kho nếu còn hold
//        Inventory inv = inventoryRepo.findByStationIdAndBatteryId(
//                booking.getStation().getId(),
//                booking.getBattery().getId()
//        ).orElse(null);
//        if (inv != null && nz(inv.getHoldQty()) > 0) {
//            inv.setHoldQty(Math.max(0, inv.getHoldQty() - 1));
//            inventoryRepo.save(inv);
//        }
//
//        return toResponse(booking);
//    }

    @Transactional
    public BookingResponse cancelBooking(Long id, String reason) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Chỉ cho phép hủy khi đang BOOKED hoặc ARRIVED
        if (!"BOOKED".equalsIgnoreCase(booking.getStatus()) &&
                !"ARRIVED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking cannot be cancelled at this stage");
        }

        booking.setStatus("CANCELLED");
        booking.setCancelReason(reason);
        booking.setCanceledAt(LocalDateTime.now());
        bookingRepo.save(booking);

        // 🔹 Cập nhật Transaction nếu có
        if (booking.getDepositTxnId() != null) {
            transactionRepo.findById(Long.parseLong(booking.getDepositTxnId())).ifPresent(txn -> {
                if ("PENDING".equalsIgnoreCase(txn.getStatus()) || "SUCCESS".equalsIgnoreCase(txn.getStatus())) {
                    txn.setStatus("REFUNDED");
                    txn.setTransactionType("REFUND");
                    txn.setRecord("Refund due to booking cancellation"); // ✅ Sửa ở đây
                    transactionRepo.save(txn);
                }
            });
        }



        // 🔹 Giải phóng hàng tồn kho
        Inventory inv = inventoryRepo.findByStationIdAndBatteryId(
                booking.getStation().getId(),
                booking.getBattery().getId()
        ).orElse(null);
        if (inv != null && nz(inv.getHoldQty()) > 0) {
            inv.setHoldQty(Math.max(0, inv.getHoldQty() - 1));
            inventoryRepo.save(inv);
        }

        return toResponse(booking);
    }


    /**
     * Đánh dấu khách đã đến trạm.
     */
    @Transactional
    public BookingResponse markArrived(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!"BOOKED".equalsIgnoreCase(booking.getStatus()))
            throw new IllegalStateException("Only booked bookings can be marked as arrived");

        booking.setStatus("ARRIVED");
        bookingRepo.save(booking);
        return toResponse(booking);
    }

    /**
     * Hoàn tất booking sau khi đổi pin thành công.
     */
    @Transactional
    public BookingResponse markCompleted(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!"ARRIVED".equalsIgnoreCase(booking.getStatus()))
            throw new IllegalStateException("Only arrived bookings can be completed");

        booking.setStatus("COMPLETED");
        bookingRepo.save(booking);
        return toResponse(booking);
    }





    @Transactional(readOnly = true)
    public MomoQRResponse generateMomoQR(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        BigDecimal amount = booking.getDepositAmount();
        String momoPhone = "0856292376";
        String message = "US" + booking.getUser().getId() + "BK" + bookingId;



        // Chuỗi QR tĩnh MoMo
        String qrContent = "2|99|" + momoPhone + "||0|" + amount.intValue() + "|Thanh toan coc|" + message;
        String qrEncoded = URLEncoder.encode(qrContent, StandardCharsets.UTF_8);

        // Tạo ảnh QR
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" + qrEncoded;

        // ✅ Trả về đầy đủ thông tin
        return new MomoQRResponse(qrUrl, momoPhone, amount, message);
    }











    @Transactional
    public BookingResponse confirmDepositManual(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!"BOOKED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking must be BOOKED to confirm deposit");
        }

        booking.setDepositStatus("PAID");
        bookingRepo.save(booking);

        // Cập nhật transaction
        Transaction tx = txnRepo.findFirstByBookingIdAndTransactionTypeOrderByTransactionTimeDesc(
                bookingId, "DEPOSIT"
        ).orElse(null);
        if (tx != null) {
            tx.setStatus("SUCCESS");
            tx.setTransactionRef("MANUAL-" + System.currentTimeMillis());
            txnRepo.save(tx);
        }

        return toResponse(booking);
    }



    // ==================== Helpers ====================

    private BookingResponse toResponse(Booking booking) {
        // --- load đầy đủ entity để tránh lazy null ---
        User user = booking.getUser() != null
                ? userRepo.findById(booking.getUser().getId()).orElse(null)
                : null;

        Station station = booking.getStation() != null
                ? stationRepo.findById(booking.getStation().getId()).orElse(null)
                : null;

        Vehicle vehicle = booking.getVehicle() != null
                ? vehicleRepo.findById(booking.getVehicle().getId()).orElse(null)
                : null;

        Battery battery = booking.getBattery() != null
                ? batteryRepo.findById(booking.getBattery().getId()).orElse(null)
                : null;

        // --- build DTO trả về ---
        return BookingResponse.builder()
                .id(booking.getId())
                .user(user == null ? null : BookingResponse.UserInfo.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .build())
                .station(station == null ? null : BookingResponse.StationInfo.builder()
                        .id(station.getId())
                        .name(station.getStationName())
                        .address(station.getAddress())
                        .status(station.getStationStatus())
                        .build())
                .vehicle(vehicle == null ? null :
                        BookingResponse.VehicleInfo.builder()
                                .id(vehicle.getId())
                                .vin(vehicle.getVin())
                                .model(vehicle.getVehicleModel())
                                .batteryType(vehicle.getBatteryType())
                                .build())
                .battery(battery == null ? null :
                        BookingResponse.BatteryInfo.builder()
                                .id(battery.getId())
                                .name(battery.getBatteryName())
                                .price(toBig(battery.getPrice()))
                                .status(battery.getStatus())
                                .build())
                .timeDate(booking.getTimeDate())
                .estimatedPrice(booking.getEstimatedPrice())
                .depositAmount(booking.getDepositAmount())
                .depositStatus(booking.getDepositStatus())
                .status(booking.getStatus())
                .holdUntil(booking.getHoldUntil())
                .cancelReason(booking.getCancelReason())
                .canceledAt(booking.getCanceledAt())
                .build();
    }


}
