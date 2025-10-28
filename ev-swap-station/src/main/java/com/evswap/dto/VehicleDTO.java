package com.evswap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Integer id;
    private String vin;
    private String vehicleModel;
    private String batteryType;

    private Integer userId;     // chỉ đưa thông tin cần dùng
    private String userName;
}
