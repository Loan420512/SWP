package com.evswap.controller;

import com.evswap.dto.ApiResponse;
import com.evswap.dto.LoginRequest;
import com.evswap.dto.LoginResponse;
import com.evswap.dto.RegisterRequest;
import com.evswap.entity.RevokedToken;
import com.evswap.entity.User;
import com.evswap.repository.RevokedTokenRepository;
import com.evswap.repository.UserRepository;
import com.evswap.security.JwtUtil;
import com.evswap.security.TokenHash;
import com.evswap.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Đăng ký thành công"));
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
//        return ResponseEntity.ok(authService.login(req));
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, Authentication auth) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new ApiResponse("Thiếu Bearer token"));
        }
        String token = header.substring(7);
        String hash = TokenHash.sha256Hex(token);

        // Lấy exp, xử lý cả token hết hạn
        Instant exp;
        try {
            var claims = jwtUtil.parse(token).getBody();
            exp = claims.getExpiration().toInstant();
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // token đã hết hạn: vẫn lưu để chặn luôn (đặt exp = now)
            exp = Instant.now();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Token không hợp lệ"));
        }

        if (!revokedTokenRepository.existsByTokenHashAndExpiresAtAfter(hash, Instant.now())) {
            revokedTokenRepository.save(RevokedToken.builder()
                    .tokenHash(hash)
                    .expiresAt(exp)
                    .username(auth != null ? auth.getName() : null)
                    .build());
        }
        return ResponseEntity.ok(new ApiResponse("Đã đăng xuất"));
    }
}

