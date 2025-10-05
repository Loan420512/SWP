package com.evswap.security;

import com.evswap.repository.RevokedTokenRepository;
import com.evswap.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final UserRepository userRepo;
    private final RevokedTokenRepository revokedRepo;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                // Chặn token đã bị revoke (và vẫn còn hạn)
                String hash = TokenHash.sha256Hex(token);
                if (revokedRepo.existsByTokenHashAndExpiresAtAfter(hash, Instant.now())) {
                    chain.doFilter(req, res);
                    return;
                }

                var claims = jwt.parse(token).getBody();
                var username = claims.getSubject();

                var user = userRepo.findByUsername(username).orElse(null);
                if (user != null) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            username, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ignored) {
                // Token không hợp lệ/hết hạn -> để anonymous
            }
        }

        chain.doFilter(req, res);
    }
}
