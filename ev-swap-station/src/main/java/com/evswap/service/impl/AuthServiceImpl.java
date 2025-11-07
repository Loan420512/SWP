package com.evswap.service.impl;

import com.evswap.dto.LoginRequest;
import com.evswap.dto.LoginResponse;
import com.evswap.dto.RegisterRequest;
import com.evswap.entity.Role;
import com.evswap.entity.Station;
import com.evswap.entity.User;
import com.evswap.repository.StationRepository;
import com.evswap.repository.UserRepository;
import com.evswap.security.JwtUtil;
import com.evswap.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final StationRepository stationRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;

    @Override
    public LoginResponse login(LoginRequest req) {
        var user = userRepo.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        String raw = req.getPassword();
        String stored = user.getPassword();

        if (looksEncoded(stored)) {
            if (!passwordEncoder.matches(raw, stored)) {
                throw new IllegalArgumentException("Sai mật khẩu");
            }
        } else {
            if (!safeEquals(stored, raw)) {
                throw new IllegalArgumentException("Sai mật khẩu");
            }
            try {
                user.setPassword(passwordEncoder.encode(raw));
                userRepo.save(user);
            } catch (Exception ignore) {
            }
        }

        Role role = user.getRole();
        String token = jwt.generate(user.getUsername(), role);
        return new LoginResponse(token, role.name(), user.getFullName());
    }

    @Override
    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepo.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        Role role = req.getRole();

        User.UserBuilder builder = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .status("Active");

        // ✅ Kiểm tra nếu là STAFF thì bắt buộc có StationID
        if (role == Role.STAFF) {
            if (req.getStationId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Staff phải thuộc một trạm (stationId không được để trống)");
            }

            Station station = stationRepo.findById(req.getStationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy trạm với ID: " + req.getStationId()));
            builder.station(station);
        }

        // ❌ Các role khác không cần Station
        if (role == Role.DRIVER || role == Role.ADMIN) {
            builder.station(null);
        }

        userRepo.save(builder.build());
    }

    private boolean looksEncoded(String s) {
        if (s == null) return false;
        if (s.startsWith("{")) return true;
        return s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$")
                || s.startsWith("$argon2") || s.startsWith("$scrypt$");
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
