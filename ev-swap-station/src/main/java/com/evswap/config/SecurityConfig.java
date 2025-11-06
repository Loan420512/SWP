package com.evswap.config;

import com.evswap.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Stateless + tắt CSRF cho API
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Swagger & tài nguyên công khai
                        .requestMatchers(
                                "/", "/error", "/favicon.ico",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()

                        // ---- Auth endpoints ----
                        // chỉ cho phép không cần token với register & login
                      .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        // chỉ cho phép không cần token với register của Driver & login
                        //.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // các auth endpoint còn lại (me, logout, ...) phải có token
                        .requestMatchers("/api/auth/**").authenticated()

                        // ---- Booking (BR1/BR2) ----
                        // tạo booking & hủy booking: Driver/Staff/Admin
                        .requestMatchers(HttpMethod.POST, "/api/bookings")
                        .hasAnyRole("DRIVER","STAFF","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel")
                        .hasAnyRole("DRIVER","STAFF","ADMIN")

                        // Cho phép preflight (CORS) nếu có front-end gọi
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Mọi endpoint khác yêu cầu xác thực
                        .anyRequest().authenticated()
                )

                // Tắt Basic Auth để tránh popup
                .httpBasic(b -> b.disable())

                // Trả JSON 401/403 gọn gàng
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"Forbidden\"}");
                        })
                )

                // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
