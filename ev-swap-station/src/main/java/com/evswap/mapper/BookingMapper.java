package com.evswap.mapper;

import com.evswap.dto.BookingResponse;
import com.evswap.entity.Battery;
import com.evswap.entity.Booking;
import com.evswap.entity.Station;
import com.evswap.entity.User;
import com.evswap.entity.Vehicle;

import java.math.BigDecimal;

/**
 * Mapper thuần Java: Entity -> BookingResponse DTO (tránh lazy proxy & null).
 */
public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponse toDto(Booking b) {
        if (b == null) return null;

        // ---- User -> UserInfo ----
        BookingResponse.UserInfo uInfo = null;
        User u = b.getUser();
        if (u != null) {
            uInfo = BookingResponse.UserInfo.builder()
                    .id(u.getId())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .email(u.getEmail())
                    .build();
        }

        // ---- Station -> StationInfo ----
        BookingResponse.StationInfo sInfo = null;
        Station s = b.getStation();
        if (s != null) {
            sInfo = BookingResponse.StationInfo.builder()
                    .id(s.getId())
                    .name(s.getStationName())
                    .address(s.getAddress())
                    .status(s.getStationStatus())
                    .build();
        }

        // ---- Vehicle -> VehicleInfo (có thể null) ----
        BookingResponse.VehicleInfo vInfo = null;
        Vehicle v = b.getVehicle();
        if (v != null) {
            vInfo = BookingResponse.VehicleInfo.builder()
                    .id(v.getId())
                    .vin(v.getVin())
                    .model(v.getVehicleModel())
                    .batteryType(v.getBatteryType())
                    .build();
        }

        // ---- Battery -> BatteryInfo ----
        BookingResponse.BatteryInfo btyInfo = null;
        Battery bt = b.getBattery();
        if (bt != null) {
            // ✅ ép kiểu Double -> BigDecimal an toàn
            BigDecimal price = bt.getPrice() != null
                    ? BigDecimal.valueOf(bt.getPrice())
                    : null;

            btyInfo = BookingResponse.BatteryInfo.builder()
                    .id(bt.getId())
                    .name(bt.getBatteryName())
                    .price(price)
                    .status(bt.getStatus())
                    .build();
        }

        // ---- Build DTO chính ----
        return BookingResponse.builder()
                .id(b.getId())                         // Long
                .user(uInfo)
                .station(sInfo)
                .vehicle(vInfo)
                .battery(btyInfo)
                .timeDate(b.getTimeDate())
                .estimatedPrice(b.getEstimatedPrice())
                .depositAmount(b.getDepositAmount())
                .depositStatus(b.getDepositStatus())
                .status(b.getStatus())
                .holdUntil(b.getHoldUntil())
                .cancelReason(b.getCancelReason())
                .canceledAt(b.getCanceledAt())
                .build();
    }
}
