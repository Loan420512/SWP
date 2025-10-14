package com.evswap.controller;

import com.evswap.dto.BookRequest;
import com.evswap.dto.BookingResponse;
import com.evswap.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * BR1 – BOOKED: đặt lịch với tiền cọc 20% (xử lý bị động).
     * - Server tự tính estimatedPrice nếu client không gửi/<=0.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> book(@RequestBody @Valid BookRequest req) {
        var res = bookingService.bookWithPassiveDeposit(
                req.getUserId(),
                req.getStationId(),
                req.getVehicleId(),
                req.getBatteryId(),
                req.getTimeSlot(),
                req.getEstimatedPrice(),
                req.getHoldMinutes() == null ? 30 : req.getHoldMinutes()
        );
        return ResponseEntity.ok(res);
    }

    // Bạn có thể thêm các endpoint cancel/arrive/complete… sau này.
}
