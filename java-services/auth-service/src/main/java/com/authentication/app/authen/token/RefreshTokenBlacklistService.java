package com.authentication.app.authen;

import java.time.Duration;

public interface RefreshTokenBlacklistService {

    void blacklist(String refreshToken, Duration ttl);

    boolean isBlacklisted(String refreshToken);
}
