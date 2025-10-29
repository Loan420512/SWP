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
                // REST API không dùng session -> stateless
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Cho phép Swagger UI và tài nguyên công khai
                        .requestMatchers(
                                "/", "/error", "/favicon.ico",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()

                        // Auth: cho phép đăng nhập / đăng ký không cần token
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()

                        // Các endpoint còn lại trong /api/auth/** cần token
                        .requestMatchers("/api/auth/**").authenticated()

                        // Vehicle API: Driver / Staff / Admin
                        .requestMatchers("/api/vehicles/**")
                        .hasAnyRole("DRIVER", "STAFF", "ADMIN")

                        // Battery API: Driver / Staff / Admin
                        .requestMatchers("/api/batteries/**")
                        .hasAnyRole("DRIVER", "STAFF", "ADMIN")

                        // Booking API: tạo & hủy booking cho các role này
                        .requestMatchers(HttpMethod.POST, "/api/bookings")
                        .hasAnyRole("DRIVER","STAFF","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel")
                        .hasAnyRole("DRIVER","STAFF","ADMIN")

                        // Cho phép preflight (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Mọi request còn lại yêu cầu xác thực
                        .anyRequest().authenticated()
                )

                // Tắt Basic Auth để tránh popup
                .httpBasic(b -> b.disable())

                // Trả JSON gọn gàng khi lỗi 401 / 403
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

                // Thêm JWT filter để xác thực token
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
