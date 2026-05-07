package com.payment.app.dao;

import com.payment.app.bean.payment.PaymentStatusHistory;
import java.sql.Timestamp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentStatusHistoryDao implements PaymentStatusHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentStatusHistoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(PaymentStatusHistory history) {
        jdbcTemplate.update(
                """
                INSERT INTO payment_status_history (id, payment_id, from_status, to_status, reason, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                history.id(),
                history.paymentId(),
                history.fromStatus() == null ? null : history.fromStatus().name(),
                history.toStatus().name(),
                history.reason(),
                Timestamp.valueOf(history.createdAt())
        );
    }
}
