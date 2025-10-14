package com.evswap.controller;

import com.evswap.dto.BookRequest;
import com.evswap.entity.Booking;
import com.evswap.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Đặt chỗ (BR1: BOOKED + deposit 20% xử lý bị động).
     * Body: BookRequest (timeSlot dạng ISO 8601 có offset: Z hoặc +07:00).
     */
    @PostMapping
    public ResponseEntity<?> book(@Valid @RequestBody BookRequest req) {
        try {
            Booking booking = bookingService.bookWithPassiveDeposit(
                    req.getUserId(),
                    req.getStationId(),
                    req.getVehicleId(),
                    req.getBatteryId(),
                    req.getTimeSlot(),                             // <-- truyền OffsetDateTime, KHÔNG convert
                    req.getEstimatedPrice(),                       // có thể null/<=0 -> service tự tính
                    req.getHoldMinutes() == null ? 30 : req.getHoldMinutes()
            );
            return ResponseEntity.ok(booking);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal error"));
        }
    }

    /**
     * Hủy đặt chỗ (BR2: CANCELING).
     * Nếu đã thanh toán cọc, service sẽ ghi giao dịch REFUND và đổi trạng thái cọc.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable("id") Long bookingId,
                                    @RequestParam(required = false) String reason) {
        try {
            Booking booking = bookingService.cancelBooking(bookingId, reason);
            return ResponseEntity.ok(booking);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal error"));
        }
    }
}
