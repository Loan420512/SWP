package com.evswap.service;

import com.evswap.entity.*;
import com.evswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;

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

    @Transactional
    public Booking bookWithPassiveDeposit(Integer userId,
                                          Integer stationId,
                                          Integer vehicleId,
                                          Integer batteryId,
                                          OffsetDateTime timeSlot,   // ISO-8601 (Z)
                                          BigDecimal estimatedPrice,
                                          int holdMinutes) {

        // ===== 1) Validate time =====
        if (timeSlot == null)
            throw new IllegalArgumentException("timeSlot required");

        Instant slotInstant = timeSlot.toInstant();
        if (slotInstant.isBefore(Instant.now()))
            throw new IllegalArgumentException("timeSlot must be in the future");

        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime slot = LocalDateTime.ofInstant(slotInstant, zone);

        if (holdMinutes <= 0)
            holdMinutes = 30;

        // ===== 2) Validate user =====
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!"Active".equalsIgnoreCase(nullToEmpty(user.getStatus())))
            throw new IllegalStateException("User is not active");

        // ===== 3) Validate station =====
        Station station = stationRepo.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));
        if (!"Open".equalsIgnoreCase(nullToEmpty(station.getStationStatus())))
            throw new IllegalStateException("Station is not open");

        // ===== 4) Validate vehicle (optional) =====
        if (vehicleId != null) {
            vehicleRepo.findByIdAndUser(vehicleId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found or not owned by user"));
        }

        // ===== 5) Validate battery =====
        Battery battery = batteryRepo.findById(batteryId)
                .orElseThrow(() -> new IllegalArgumentException("Battery not found"));

        String bStatus = nullToEmpty(battery.getStatus());
        if (!"Full".equalsIgnoreCase(bStatus))
            throw new IllegalStateException("Battery must be Full to book");

        // ===== 6) Calculate price if needed =====
        if (estimatedPrice == null || estimatedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            Double priceD = battery.getPrice();
            if (priceD == null || priceD <= 0)
                throw new IllegalStateException("Battery price not configured");
            estimatedPrice = BigDecimal.valueOf(priceD);
        }

        // ===== 7) Prevent overlapping booking =====
        long overlap = bookingRepo.countOpenAround(userId, slot, 30);
        if (overlap > 0)
            throw new IllegalStateException("You already have a booking around that time");

        // ===== 8) Lock inventory =====
        Inventory inv = inventoryRepo.lockForUpdate(stationId, batteryId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));
        int available = nz(inv.getReadyQty()) - nz(inv.getHoldQty());
        if (available <= 0)
            throw new IllegalStateException("Out of stock for this station/battery");

        inv.setHoldQty(nz(inv.getHoldQty()) + 1);
        inventoryRepo.save(inv);

        // ===== 9) Deposit (20%) =====
        BigDecimal deposit = estimatedPrice.multiply(new BigDecimal("0.20"))
                .setScale(0, RoundingMode.HALF_UP);

        // ===== 10) Save booking =====
        Booking b = Booking.builder()
                .user(User.builder().id(userId).build())
                .station(Station.builder().id(stationId).build())
                .vehicle(vehicleId != null ? Vehicle.builder().id(vehicleId).build() : null)
                .battery(Battery.builder().id(batteryId).build())
                .timeDate(slot)
                .estimatedPrice(estimatedPrice)
                .depositAmount(deposit)
                .depositStatus("PENDING")
                .status("BOOKED")
                .holdUntil(LocalDateTime.now(zone).plusMinutes(holdMinutes))
                .build();
        b = bookingRepo.save(b);

        // ===== 11) Save deposit transaction =====
        Transaction tx = Transaction.builder()
                .user(User.builder().id(userId).build())
                .station(Station.builder().id(stationId).build())
                .booking(b)
                .amount(deposit)
                .transactionType("DEPOSIT")
                .status("PENDING")
                .transactionTime(LocalDateTime.now(zone))
                .build();
        txnRepo.save(tx);

        return b;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, String reason) {
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!"BOOKED".equalsIgnoreCase(b.getStatus()) &&
                !"ARRIVED".equalsIgnoreCase(b.getStatus())) {
            throw new IllegalStateException("Only BOOKED/ARRIVED can be cancelled");
        }

        // Giải phóng holdQty
        if (b.getStation() != null && b.getBattery() != null) {
            inventoryRepo.lockForUpdate(b.getStation().getId(), b.getBattery().getId())
                    .ifPresent(inv -> {
                        int hold = nz(inv.getHoldQty());
                        if (hold > 0) {
                            inv.setHoldQty(hold - 1);
                            inventoryRepo.save(inv);
                        }
                    });
        }

        b.setStatus("CANCELLED");
        b.setCancelReason(reason);
        b.setCanceledAt(LocalDateTime.now());
        bookingRepo.save(b);

        if ("PAID".equalsIgnoreCase(b.getDepositStatus())) {
            Transaction refund = Transaction.builder()
                    .user(b.getUser())
                    .station(b.getStation())
                    .booking(b)
                    .amount(b.getDepositAmount())
                    .transactionType("REFUND")
                    .status("SUCCESS")
                    .transactionTime(LocalDateTime.now())
                    .build();
            txnRepo.save(refund);

            b.setDepositStatus("REFUNDED");
            bookingRepo.save(b);
        }
        return b;
    }

    private int nz(Integer v) { return v == null ? 0 : v; }

    private String nullToEmpty(String s) { return s == null ? "" : s.trim(); }
}
