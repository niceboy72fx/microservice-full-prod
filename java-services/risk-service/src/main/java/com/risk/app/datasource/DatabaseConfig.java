package com.risk.app.datasource;

public record DatabaseConfig(
        String jdbcUrl,
        String username,
        String password
) {}
