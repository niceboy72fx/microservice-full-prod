package com.authentication.app.security.token;

import com.commonservice.common.cache.CacheService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedisRefreshTokenBlacklistService implements RefreshTokenBlacklistService {

    private final CacheService cacheService;
    private final String keyPrefix;

    public RedisRefreshTokenBlacklistService(
            CacheService cacheService,
            @Value("${app.auth.refresh-blacklist-prefix:auth:refresh:blacklist:}") String keyPrefix
    ) {
        this.cacheService = cacheService;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void blacklist(String refreshToken, Duration ttl) {
        if (refreshToken == null || refreshToken.isBlank() || ttl == null || ttl.isNegative() || ttl.isZero()) {
            return;
        }
        cacheService.put(buildKey(refreshToken), "1", ttl);
    }

    @Override
    public boolean isBlacklisted(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return false;
        }
        return cacheService.exists(buildKey(refreshToken));
    }

    private String buildKey(String refreshToken) {
        return keyPrefix + sha256(refreshToken);
    }

    private String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }
}
