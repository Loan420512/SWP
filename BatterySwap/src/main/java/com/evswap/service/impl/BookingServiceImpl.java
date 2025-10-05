package com.evswap.service.impl;

import com.evswap.dto.booking.BookingCreateRequest;
import com.evswap.dto.booking.BookingResponse;
import com.evswap.entity.*;
import com.evswap.repository.*;
import com.evswap.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final StationRepository stationRepo;
    private final VehicleRepository vehicleRepo;
    private final BatteryRepository batteryRepo;
    private final UserRepository userRepo;
    private final TransactionRepository txnRepo;

    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.20");

    @Transactional
    @Override
    public BookingResponse create(BookingCreateRequest req, String username) {
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        var station = stationRepo.findById(req.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station không tồn tại"));
        var vehicle = vehicleRepo.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle không tồn tại"));
        var battery = batteryRepo.findById(req.getBatteryId())
                .orElseThrow(() -> new IllegalArgumentException("Battery không tồn tại"));

        // Giá ước tính lấy theo Battery.Price
        var estimated = battery.getPrice();
        var deposit = estimated.multiply(DEPOSIT_RATE).setScale(2, RoundingMode.HALF_UP);

        // Giao dịch đặt cọc (PENDING – xử lý bị động)
        var txn = TransactionEntity.builder()
                .user(user)
                .station(station)
                .timeDate(LocalDateTime.now())
                .record("BOOKING_DEPOSIT_PENDING")
                .amount(deposit)
                .build();
        txn = txnRepo.save(txn);

        var bk = Booking.builder()
                .station(station)
                .user(user)
                .vehicle(vehicle)
                .battery(battery)
                .timeDate(req.getTimeDate())
                .estimatedPrice(estimated)
                .depositAmount(deposit)
                .depositStatus("PENDING")
                .status("BOOKED")
                .depositTxn(txn)
                .build();
        bk = bookingRepo.save(bk);

        return new BookingResponse(
                bk.getId(), bk.getStatus(), bk.getEstimatedPrice(), bk.getDepositAmount(), bk.getDepositStatus()
        );
    }

    @Transactional
    @Override
    public void cancel(Integer bookingId, String username, String reason) {
        var bk = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại"));

        var user = userRepo.findByUsername(username).orElseThrow();
        boolean isOwner = bk.getUser().getId().equals(user.getId());
        boolean staffOrAdmin = user.getRole() == Role.Staff || user.getRole() == Role.Admin;
        if (!isOwner && !staffOrAdmin) {
            throw new IllegalArgumentException("Không có quyền hủy");
        }

        if (!"BOOKED".equals(bk.getStatus())) {
            throw new IllegalArgumentException("Chỉ hủy được khi trạng thái BOOKED");
        }

        // Chính sách cọc
        if ("PENDING".equals(bk.getDepositStatus())) {
            bk.setDepositStatus("WAIVED");
            if (bk.getDepositTxn() != null) {
                bk.getDepositTxn().setRecord("BOOKING_DEPOSIT_WAIVED");
            }
        } else if ("PAID".equals(bk.getDepositStatus())) {
            long hoursBefore = Duration.between(LocalDateTime.now(), bk.getTimeDate()).toHours();
            if (hoursBefore >= 2) {
                bk.setDepositStatus("REFUNDED");
                // ghi 1 giao dịch âm cho refund
                var refund = TransactionEntity.builder()
                        .user(bk.getUser())
                        .station(bk.getStation())
                        .timeDate(LocalDateTime.now())
                        .record("BOOKING_DEPOSIT_REFUND")
                        .amount(bk.getDepositAmount().negate())
                        .build();
                txnRepo.save(refund);
            } else {
                // Giữ cọc nếu hủy quá sát giờ (tuỳ rule)
            }
        }

        bk.setStatus("CANCELLED");
        bk.setCancelReason(reason);
        bk.setCanceledAt(LocalDateTime.now());

        bookingRepo.save(bk);
    }
}
