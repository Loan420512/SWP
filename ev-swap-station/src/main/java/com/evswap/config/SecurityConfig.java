package com.evswap.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Bật CORS - sử dụng config từ WebConfig
                .cors(cors -> {})
                // ✅ Tắt CSRF cho REST API
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/api/auth/**"
                        ).permitAll()
<<<<<<< HEAD

                        // ---- Auth endpoints ----
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").authenticated()

                        // ---- Vehicle ----
                        .requestMatchers("/api/vehicles/**").authenticated()

                        // ---- Battery ----
                        .requestMatchers("/api/batteries/**").authenticated()

                        // ---- Booking (BR1/BR2) ----
                        .requestMatchers(HttpMethod.POST, "/api/bookings")
                        .hasAnyRole("DRIVER","STAFF","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel")
                        .hasAnyRole("DRIVER","STAFF","ADMIN")

                        // Cho phép preflight (CORS) nếu có front-end gọi
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Mọi endpoint khác yêu cầu xác thực
=======
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // Cho phép OPTIONS
>>>>>>> 5d441ebf0ec6f3b7b8c6d708001a606ed675491a
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
