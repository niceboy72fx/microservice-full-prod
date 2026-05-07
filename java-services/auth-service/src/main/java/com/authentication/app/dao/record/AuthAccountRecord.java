package com.authentication.app.dao;

public record AuthAccountRecord(
        String id,
        String email,
        String password
) {
}
