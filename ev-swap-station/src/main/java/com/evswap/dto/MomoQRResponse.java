package com.evswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomoQRResponse {
    private String qrUrl;      // link ảnh QR
    private String receiver;   // số điện thoại MoMo nhận
    private BigDecimal amount; // số tiền
    private String message;    // nội dung chuyển khoản
}
