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
        var user = userRepo.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

//        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("Sai mật khẩu");
//        }
        String raw = req.getPassword();
        String stored = user.getPassword();

        if (looksEncoded(stored)) {
            // Trường hợp DB đã lưu hash (bcrypt/argon2/…)
            if (!passwordEncoder.matches(raw, stored)) {
                throw new IllegalArgumentException("Sai mật khẩu");
            }
        } else {
            // Trường hợp DB đang lưu plaintext (dữ liệu cứng)
            if (!safeEquals(stored, raw)) {
                throw new IllegalArgumentException("Sai mật khẩu");
            }
            // (Tuỳ chọn nhưng nên có) Tự động migrate sang hash sau lần login đầu tiên
            try {
                user.setPassword(passwordEncoder.encode(raw));
                // đổi "userRepository" cho đúng tên repo đang @Autowired trong class của bạn
                userRepo.save(user);
            } catch (Exception ignore) {
                // nếu bạn chưa muốn cập nhật tại đây, có thể bỏ cả khối try-catch này
            }
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
        if (userRepo.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
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

    /** Nhận diện chuỗi có vẻ là mật khẩu đã mã hoá */
    private boolean looksEncoded(String s) {
        if (s == null) return false;
        // DelegatingPasswordEncoder format: {id}hash
        if (s.startsWith("{")) return true;
        // Một số prefix thông dụng
        return s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$")  // BCrypt
                || s.startsWith("$argon2")                                              // Argon2
                || s.startsWith("$scrypt$");                                            // scrypt
    }

    /** So sánh tránh timing leak (đủ dùng cho plaintext legacy) */
    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }

}
