package com.evswap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class BookRequest {

    /** ID người dùng thực hiện đặt chỗ */
    @NotNull
    private Integer userId;

    /** ID trạm thực hiện đổi pin */
    @NotNull
    private Integer stationId;

    /** ID phương tiện (optional – có thể null nếu user chưa đăng ký) */
    private Integer vehicleId;

    /** ID pin muốn đổi */
    @NotNull
    private Integer batteryId;

    /**
     * Thời gian đặt chỗ, có offset/timezone rõ ràng.
     * VD: "2025-10-15T03:00:00Z" hoặc "2025-10-15T10:00:00+07:00"
     */
    @NotNull
    private OffsetDateTime timeSlot;

    /**
     * Giá ước tính — có thể null hoặc <=0, khi đó service sẽ tự tính từ giá pin.
     */
    private BigDecimal estimatedPrice;

    /**
     * Thời gian giữ chỗ (phút), mặc định = 30 nếu null.
     * Giới hạn từ 5 đến 240 phút để tránh spam giữ chỗ.
     */
    @Min(5)
    @Max(240)
    private Integer holdMinutes;
}
