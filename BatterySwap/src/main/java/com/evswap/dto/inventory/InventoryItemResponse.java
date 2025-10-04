package com.evswap.dto.inventory;
public record InventoryItemResponse(
        Integer inventoryId,
        Integer stationId, String stationName,
        Integer batteryId, String batteryName,
        String batteryStatus, String inventoryStatus) {}
