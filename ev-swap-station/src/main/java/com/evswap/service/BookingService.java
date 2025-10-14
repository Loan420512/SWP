package com.evswap.service;

import com.evswap.dto.BookingResponse;
import com.evswap.entity.*;
import com.evswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
}
