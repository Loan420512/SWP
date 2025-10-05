package com.evswap.dto.inventory;
import jakarta.validation.constraints.NotBlank;
public record InventoryUpdateStatusRequest(@NotBlank String status) {}
