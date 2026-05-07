package com.authentication.app.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoginAuditSqlRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoginAuditSqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String email, String ipAddress, boolean success, String reason) {
        jdbcTemplate.update(
                """
                INSERT INTO app_user_login_audit (email, ip_address, success, reason, create_at)
                VALUES (?, ?, ?, ?, SYSTIMESTAMP)
                """,
                email,
                ipAddress,
                success ? 1 : 0,
                reason
        );
    }
}
