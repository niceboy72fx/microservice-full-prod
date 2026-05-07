package com.payment.app.dao;

import com.payment.app.bean.payment.Payment;
import com.payment.app.bean.payment.PaymentMethod;
import com.payment.app.bean.payment.PaymentProviderType;
import com.payment.app.bean.payment.PaymentStatus;
import com.payment.app.bean.payment.PaymentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao implements PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Payment save(Payment payment) {
        int updated = jdbcTemplate.update(
                """
                UPDATE payments SET
                    status = ?,
                    provider_transaction_id = ?,
                    failure_reason = ?,
                    updated_at = ?
                WHERE id = ?
                """,
                payment.getStatus().name(),
                payment.getProviderTransactionId(),
                payment.getFailureReason(),
                Timestamp.valueOf(payment.getUpdatedAt()),
                payment.getId()
        );

        if (updated == 0) {
            try {
                jdbcTemplate.update(
                        """
                        INSERT INTO payments (
                            id, user_id, type, amount, currency, method, status, provider,
                            provider_transaction_id, idempotency_key, correlation_id, failure_reason,
                            created_at, updated_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        payment.getId(),
                        payment.getUserId(),
                        payment.getType().name(),
                        payment.getAmount(),
                        payment.getCurrency(),
                        payment.getMethod().name(),
                        payment.getStatus().name(),
                        payment.getProvider().name(),
                        payment.getProviderTransactionId(),
                        payment.getIdempotencyKey(),
                        payment.getCorrelationId(),
                        payment.getFailureReason(),
                        Timestamp.valueOf(payment.getCreatedAt()),
                        Timestamp.valueOf(payment.getUpdatedAt())
                );
            } catch (DuplicateKeyException exception) {
                throw exception;
            }
        }
        return payment;
    }

    @Override
    public Optional<Payment> findById(String id) {
        List<Payment> results = jdbcTemplate.query(
                """
                SELECT id, user_id, type, amount, currency, method, status, provider,
                       provider_transaction_id, idempotency_key, correlation_id, failure_reason,
                       created_at, updated_at
                FROM payments WHERE id = ?
                """,
                this::mapRow,
                id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Payment> findByUserIdAndIdempotencyKey(String userId, String idempotencyKey) {
        List<Payment> results = jdbcTemplate.query(
                """
                SELECT id, user_id, type, amount, currency, method, status, provider,
                       provider_transaction_id, idempotency_key, correlation_id, failure_reason,
                       created_at, updated_at
                FROM payments WHERE user_id = ? AND idempotency_key = ?
                """,
                this::mapRow,
                userId,
                idempotencyKey
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    private Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Payment(
                rs.getString("id"),
                rs.getString("user_id"),
                PaymentType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                PaymentMethod.valueOf(rs.getString("method")),
                PaymentStatus.valueOf(rs.getString("status")),
                PaymentProviderType.valueOf(rs.getString("provider")),
                rs.getString("provider_transaction_id"),
                rs.getString("idempotency_key"),
                rs.getString("correlation_id"),
                rs.getString("failure_reason"),
                toLocalDateTime(rs.getTimestamp("created_at")),
                toLocalDateTime(rs.getTimestamp("updated_at"))
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
