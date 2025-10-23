package com.evswap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmRequest {
    @NotBlank
    private String txnRef; // Mã giao dịch từ cổng thanh toán
}
