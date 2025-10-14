package com.evswap.service.impl;

import com.evswap.dto.LoginRequest;
import com.evswap.dto.LoginResponse;
import com.evswap.dto.RegisterRequest;
import com.evswap.entity.Role;
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

        // role là enum
        Role role = user.getRole();

        // Sinh JWT theo chữ ký: generate(username, Role)
        String token = jwt.generate(user.getUsername(), role);

        // Trả về role dạng chuỗi cho client (ví dụ: "ADMIN")
        return new LoginResponse(token, role.name(), user.getFullName());
    }

    @Override
    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        // Lấy role từ request (enum), nếu muốn mặc định có thể xử lý ở layer DTO/Controller
        Role role = req.getRole();

        var u = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role) // enum
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .status("Active")
                .build();

        userRepo.save(u);
    }
}
