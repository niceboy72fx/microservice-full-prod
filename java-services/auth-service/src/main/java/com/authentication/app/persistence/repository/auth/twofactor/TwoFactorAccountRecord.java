package com.authentication.app.persistence.repository.auth.twofactor;

public record TwoFactorAccountRecord(
        String email,
        String secret,
        boolean enabled
) {
}
