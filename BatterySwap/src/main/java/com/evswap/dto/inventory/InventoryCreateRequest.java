package com.evswap.dto.inventory;
import jakarta.validation.constraints.*;
public record InventoryCreateRequest(@NotNull Integer stationId, @NotNull Integer batteryId,
                                     @NotBlank String status) {}
