package com.evswap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "RevokedToken", indexes = {
        @Index(name = "idx_revoked_token_hash", columnList = "tokenHash")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lưu SHA-256 của token để an toàn
    @Column(nullable = false, length = 64)
    private String tokenHash;

    // Hạn của token (exp)
    @Column(nullable = false)
    private Instant expiresAt;

    // Ai thực hiện logout (optional)
    private String username;
}
