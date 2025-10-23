package com.evswap.controller;

import com.evswap.dto.PaymentConfirmRequest;
import com.evswap.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý callback hoặc confirm từ cổng thanh toán (Momo, VNPay, v.v.)
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BookingService bookingService;

    @Operation(summary = "Callback từ cổng thanh toán",
            description = "Nhận dữ liệu xác nhận từ hệ thống thanh toán.")
    @PostMapping("/deposit/{bookingId}/confirm")
    public ResponseEntity<?> confirmDeposit(@PathVariable Long bookingId,
                                            @RequestBody PaymentConfirmRequest req) {
        return ResponseEntity.ok(bookingService.confirmDeposit(bookingId, req.getTxnRef()));
    }
}
