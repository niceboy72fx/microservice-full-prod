package com.authentication.app.dao;

import com.authentication.app.dao.record.AuthAccountRecord;
import java.util.List;
import java.util.Optional;
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
        // id uses DB default: RAWTOHEX(SYS_GUID()) from migrate.sql
        jdbcTemplate.update(
                """
                INSERT INTO app_user (full_name, email, password, status, create_at, update_at)
                VALUES (?, ?, ?, 'ACTIVE', SYSTIMESTAMP, SYSTIMESTAMP)
                """,
                email,
                email,
                encodedPassword
        );

        AuthAccountRecord account = findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Inserted user not found: " + email));

        // assign USER role if exists
        List<String> roleIds = jdbcTemplate.query(
                "SELECT id FROM role WHERE name = 'USER'",
                (rs, rowNum) -> rs.getString("id")
        );

        if (!roleIds.isEmpty()) {
            String roleId = roleIds.get(0);
            Integer linked = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM user_role WHERE user_id = ? AND role_id = ?",
                    Integer.class,
                    account.id(),
                    roleId
            );
            if (linked == null || linked == 0) {
                jdbcTemplate.update(
                        """
                        INSERT INTO user_role (user_id, role_id, create_at, update_at)
                        VALUES (?, ?, SYSTIMESTAMP, SYSTIMESTAMP)
                        """,
                        account.id(),
                        roleId
                );
            }
        }

        return account;
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
