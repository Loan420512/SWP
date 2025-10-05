package com.evswap.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data @AllArgsConstructor
public class BookingResponse {
    private Integer bookingId;
    private String status;
    private BigDecimal estimatedPrice;
    private BigDecimal depositAmount;
    private String depositStatus;
}
