package com.authentication.app.dao;

import com.authentication.app.dao.record.TwoFactorAccountRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TwoFactorSqlRepository {

    private final JdbcTemplate jdbcTemplate;

    public TwoFactorSqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TwoFactorAccountRecord> findByEmail(String email) {
        List<TwoFactorAccountRecord> rows = jdbcTemplate.query(
                "SELECT email, secret, enabled FROM app_user_2fa WHERE email = ?",
                (rs, rowNum) -> new TwoFactorAccountRecord(
                        rs.getString("email"),
                        rs.getString("secret"),
                        rs.getInt("enabled") == 1
                ),
                email
        );
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rows.get(0));
    }

    public void upsertSecret(String email, String secret) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM app_user_2fa WHERE email = ?",
                Integer.class,
                email
        );
        if (count != null && count > 0) {
            jdbcTemplate.update(
                    "UPDATE app_user_2fa SET secret = ?, enabled = 0, update_at = SYSTIMESTAMP WHERE email = ?",
                    secret,
                    email
            );
            return;
        }
        jdbcTemplate.update(
                """
                INSERT INTO app_user_2fa (email, secret, enabled, create_at, update_at)
                VALUES (?, ?, 0, SYSTIMESTAMP, SYSTIMESTAMP)
                """,
                email,
                secret
        );
    }

    public void setEnabled(String email, boolean enabled) {
        jdbcTemplate.update(
                "UPDATE app_user_2fa SET enabled = ?, update_at = SYSTIMESTAMP WHERE email = ?",
                enabled ? 1 : 0,
                email
        );
    }
}
