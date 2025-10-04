package com.evswap.config;

import com.evswap.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class CleanupConfig {
    private final RevokedTokenRepository repo;

    // 02:30 mỗi ngày
    @Scheduled(cron = "0 30 2 * * *")
    public void purgeExpired() {
        repo.deleteByExpiresAtBefore(Instant.now());
    }
}
