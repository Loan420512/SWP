package com.evswap.service;

import com.evswap.dto.auth.*;

public interface AuthService {
    LoginResponse login(LoginRequest req);
    void register(RegisterRequest req);
}
