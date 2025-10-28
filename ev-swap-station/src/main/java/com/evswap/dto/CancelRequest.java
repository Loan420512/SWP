package com.evswap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelRequest {
    @NotBlank
    private String reason;
}
