package com.ledger.app.config;

public record DatabaseConfig(
        String jdbcUrl,
        String username,
        String password
) {}
