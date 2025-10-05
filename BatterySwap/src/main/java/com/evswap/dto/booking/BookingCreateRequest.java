package com.evswap.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateRequest {
    @NotNull private Integer stationId;
    @NotNull private Integer vehicleId;
    @NotNull private Integer batteryId;
    @NotNull private LocalDateTime timeDate;
}
