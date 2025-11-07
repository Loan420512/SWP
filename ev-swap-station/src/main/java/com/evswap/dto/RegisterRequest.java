package com.evswap.dto;

import com.evswap.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotNull  private Role role; // Driver/Staff/Admin
    @NotBlank private String fullName;
    @NotBlank private String phone;
    @NotBlank private String email;
    @NotBlank private String address;
    private Integer stationId;
}


