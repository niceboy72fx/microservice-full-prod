package com.ledger.app.datasource;

public record DatabaseConfig(
        String jdbcUrl,
        String username,
        String password
) {}
