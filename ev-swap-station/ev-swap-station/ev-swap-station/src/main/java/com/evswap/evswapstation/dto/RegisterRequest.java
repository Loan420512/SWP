package com.evswap.evswapstation.dto;

import com.evswap.evswapstation.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
//public class RegisterRequest {
//    private String userName;
//    private String password;
//    private String fullName;
//    private String phone;
//    private String email;
//    private String address;
//}

public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank private String password;
    @NotNull  private Role role; // Driver/Staff/Admin
    @NotBlank private String fullName;
    @NotBlank private String phone;
    @NotBlank private String email;
    @NotBlank private String address;
}

