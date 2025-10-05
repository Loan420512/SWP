package com.evswap.controller;

import com.evswap.dto.ApiResponse;
import com.evswap.dto.booking.BookingCreateRequest;
import com.evswap.dto.booking.BookingResponse;
import com.evswap.dto.booking.CancelBookingRequest;
import com.evswap.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingCreateRequest req,
                                                  Authentication auth) {
        var res = bookingService.create(req, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancel(@PathVariable Integer id,
                                              @Valid @RequestBody CancelBookingRequest body,
                                              Authentication auth) {
        bookingService.cancel(id, auth.getName(), body.getReason());
        return ResponseEntity.ok(new ApiResponse("Đã hủy booking"));
    }
}
