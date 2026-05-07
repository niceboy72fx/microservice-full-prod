package com.authentication.app.dao;

public record TwoFactorAccountRecord(
        String email,
        String secret,
        boolean enabled
) {
}
