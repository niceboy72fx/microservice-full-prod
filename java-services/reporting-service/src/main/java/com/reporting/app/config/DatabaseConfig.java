package com.reporting.app.config;

public record DatabaseConfig(
        String jdbcUrl,
        String username,
        String password
) {}
