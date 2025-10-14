package com.evswap.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;

    private Integer userId;
    private Integer stationId;
    private Integer vehicleId;  // nullable
    private Integer batteryId;  // nullable

    private LocalDateTime timeDate;

    private BigDecimal estimatedPrice;
    private BigDecimal depositAmount;
    private String depositStatus;  // PENDING/PAID/REFUNDED
    private String status;         // BOOKED/ARRIVED/SWAPPED/CANCELLED/NO_SHOW

    private LocalDateTime holdUntil;

    private String cancelReason;
    private LocalDateTime canceledAt;
}
