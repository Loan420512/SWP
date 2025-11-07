package com.evswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private Integer id;
    private String vin;
    private String vehicleModel;
    private String batteryType;
    private String registerInformation;   // ✅ thêm field bị thiếu

    private Integer userId;

    @Schema(hidden = true)
    private String userName;
}
