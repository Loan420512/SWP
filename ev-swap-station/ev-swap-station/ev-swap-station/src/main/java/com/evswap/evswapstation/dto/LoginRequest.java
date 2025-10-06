package com.evswap.evswapstation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
//public class LoginRequest {
//    private String userName;
//    private String password;
//}

public class LoginRequest { @NotBlank
private String username; @NotBlank private String password; }
