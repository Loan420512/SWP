package com.evswap.service;

import com.evswap.dto.LoginRequest;
import com.evswap.dto.LoginResponse;
import com.evswap.dto.RegisterRequest;
import com.evswap.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest req);
    void register(RegisterRequest req);
}

