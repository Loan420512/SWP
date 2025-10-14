package com.evswap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Yêu cầu đặt lịch đổi pin.
 * - timeSlot gửi kèm offset: ví dụ "2025-10-15T10:00:00+07:00" hoặc "2025-10-15T03:00:00Z"
 * - estimatedPrice có thể null/<=0: server sẽ tự tính theo giá pin.
 */
@Data
public class BookRequest {
    @NotNull private Integer userId;
    @NotNull private Integer stationId;
    private Integer vehicleId;                 // optional
    @NotNull private Integer batteryId;

    @NotNull
    private OffsetDateTime timeSlot;

    private BigDecimal estimatedPrice;         // cho phép null/<=0

    @Min(5) @Max(240)
    private Integer holdMinutes;               // nếu null → default 30
}
