package com.evswap.service.impl;

import com.evswap.dto.LoginRequest;
import com.evswap.dto.LoginResponse;
import com.evswap.dto.RegisterRequest;
import com.evswap.entity.User;
import com.evswap.repository.UserRepository;
import com.evswap.security.JwtUtil;
import com.evswap.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder; // inject từ SecurityConfig
    private final JwtUtil jwt;

    @Override
    public LoginResponse login(LoginRequest req) {
        var user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Sai mật khẩu");
        }

        String token = jwt.generate(user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getRole().name(), user.getFullName());
    }

    @Override
    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        var u = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .build();

        userRepo.save(u);
    }
}


