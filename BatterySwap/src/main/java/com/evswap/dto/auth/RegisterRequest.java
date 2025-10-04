package com.evswap.dto.auth;

import com.evswap.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotNull  private Role role; // Driver/Staff/Admin
    private String fullName; private String phone; private String email; private String address;
}
