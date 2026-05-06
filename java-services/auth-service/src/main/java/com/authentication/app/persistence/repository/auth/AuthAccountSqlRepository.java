package com.authentication.app.persistence.repository.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthAccountSqlRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthAccountSqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM app_user WHERE email = ?",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }

    public AuthAccountRecord insert(String email, String encodedPassword) {
        String id = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        jdbcTemplate.update(
                """
                INSERT INTO app_user (id, full_name, email, password, status, create_at, update_at)
                VALUES (?, ?, ?, ?, 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP)
                """,
                id,
                email,
                email,
                encodedPassword
        );
        return new AuthAccountRecord(id, email, encodedPassword);
    }

    public Optional<AuthAccountRecord> findByEmail(String email) {
        List<AuthAccountRecord> results = jdbcTemplate.query(
                "SELECT id, email, password FROM app_user WHERE email = ?",
                (rs, rowNum) -> new AuthAccountRecord(
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getString("password")
                ),
                email
        );
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}
