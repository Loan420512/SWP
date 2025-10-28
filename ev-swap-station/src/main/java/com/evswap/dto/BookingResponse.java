package com.evswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO trả về cho màn booking. Tránh trả entity -> tránh lazy proxy & null.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;  // ✅ sửa từ Integer -> Long

    private UserInfo user;
    private StationInfo station;
    private VehicleInfo vehicle;   // có thể null
    private BatteryInfo battery;

    private LocalDateTime timeDate;
    private BigDecimal estimatedPrice;
    private BigDecimal depositAmount;
    private String depositStatus;  // PENDING/PAID/REFUNDED
    private String status;         // BOOKED/ARRIVED/...
    private LocalDateTime holdUntil;

    private String cancelReason;
    private LocalDateTime canceledAt;

    // ---- embedded infos ----
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String fullName;
        private String phone;
        private String email;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StationInfo {
        private Integer id;
        private String name;
        private String address;
        private String status;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VehicleInfo {
        private Integer id;
        private String vin;
        private String model;
        private String batteryType;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BatteryInfo {
        private Integer id;
        private String name;
        private BigDecimal price;
        private String status;
    }
}
