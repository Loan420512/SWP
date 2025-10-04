package com.evswap.dto.inventory;
import jakarta.validation.constraints.NotNull;
public record InventoryTransferRequest(@NotNull Integer fromStationId,
                                       @NotNull Integer toStationId,
                                       @NotNull Integer batteryId) {}
