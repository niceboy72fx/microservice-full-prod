package com.authentication.app.persistence.repository.auth;

public record AuthAccountRecord(
        String id,
        String email,
        String password
) {
}
