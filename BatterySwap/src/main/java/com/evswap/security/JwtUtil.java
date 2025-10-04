package com.evswap.security;

import com.evswap.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expirationMs}") private long expiration;

    public String generate(String username, Role role) {
        Date now=new Date(); Date exp=new Date(now.getTime()+expiration);
        return Jwts.builder()
                .setSubject(username).claim("role", role.name())
                .setIssuedAt(now).setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token);
    }
}
