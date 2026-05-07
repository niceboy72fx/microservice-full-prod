package com.payment.app.bean;

import com.payment.app.common.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {

    private final String id;
    private final String userId;
    private final PaymentType type;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentMethod method;
    private PaymentStatus status;
    private final PaymentProviderType provider;
    private String providerTransactionId;
    private final String idempotencyKey;
    private final String correlationId;
    private String failureReason;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(
            String id,
            String userId,
            PaymentType type,
            BigDecimal amount,
            String currency,
            PaymentMethod method,
            PaymentStatus status,
            PaymentProviderType provider,
            String providerTransactionId,
            String idempotencyKey,
            String correlationId,
            String failureReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.type = Objects.requireNonNull(type, "type is required");
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.currency = Objects.requireNonNull(currency, "currency is required");
        this.method = Objects.requireNonNull(method, "method is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.provider = Objects.requireNonNull(provider, "provider is required");
        this.providerTransactionId = providerTransactionId;
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey is required");
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId is required");
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Payment newCreated(
            String id,
            String userId,
            PaymentType type,
            BigDecimal amount,
            String currency,
            PaymentMethod method,
            PaymentProviderType provider,
            String idempotencyKey,
            String correlationId,
            LocalDateTime now
    ) {
        return new Payment(
                id,
                userId,
                type,
                amount,
                currency,
                method,
                PaymentStatus.CREATED,
                provider,
                null,
                idempotencyKey,
                correlationId,
                null,
                now,
                now
        );
    }

    public PaymentStatus transitionToProcessing(LocalDateTime now) {
        assertTransition(PaymentStatus.CREATED, PaymentStatus.PROCESSING);
        this.status = PaymentStatus.PROCESSING;
        this.updatedAt = now;
        return this.status;
    }

    public PaymentStatus transitionToSuccess(String providerTransactionId, LocalDateTime now) {
        assertTransitionFromSet(PaymentStatus.SUCCESS, PaymentStatus.PROCESSING, PaymentStatus.UNKNOWN);
        this.status = PaymentStatus.SUCCESS;
        this.providerTransactionId = providerTransactionId;
        this.failureReason = null;
        this.updatedAt = now;
        return this.status;
    }

    public PaymentStatus transitionToFailed(String reason, String providerTransactionId, LocalDateTime now) {
        assertTransitionFromSet(PaymentStatus.FAILED, PaymentStatus.PROCESSING, PaymentStatus.UNKNOWN);
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.providerTransactionId = providerTransactionId;
        this.updatedAt = now;
        return this.status;
    }

    public PaymentStatus transitionToUnknown(String reason, String providerTransactionId, LocalDateTime now) {
        assertTransition(PaymentStatus.PROCESSING, PaymentStatus.UNKNOWN);
        this.status = PaymentStatus.UNKNOWN;
        this.failureReason = reason;
        this.providerTransactionId = providerTransactionId;
        this.updatedAt = now;
        return this.status;
    }

    public PaymentStatus transitionToCancelled(boolean externallySubmitted, String reason, LocalDateTime now) {
        if (this.status == PaymentStatus.CREATED) {
            this.status = PaymentStatus.CANCELLED;
            this.failureReason = reason;
            this.updatedAt = now;
            return this.status;
        }
        if (this.status == PaymentStatus.PROCESSING && !externallySubmitted) {
            this.status = PaymentStatus.CANCELLED;
            this.failureReason = reason;
            this.updatedAt = now;
            return this.status;
        }
        throw invalidTransition(PaymentStatus.CANCELLED);
    }

    private void assertTransition(PaymentStatus from, PaymentStatus to) {
        if (this.status != from) {
            throw invalidTransition(to);
        }
    }

    private void assertTransitionFromSet(PaymentStatus to, PaymentStatus firstAllowed, PaymentStatus secondAllowed) {
        if (this.status != firstAllowed && this.status != secondAllowed) {
            throw invalidTransition(to);
        }
    }

    private BusinessException invalidTransition(PaymentStatus to) {
        return new BusinessException("INVALID_PAYMENT_STATE", "Cannot transition payment " + id + " from " + status + " to " + to);
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public PaymentType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public PaymentProviderType getProvider() { return provider; }
    public String getProviderTransactionId() { return providerTransactionId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getCorrelationId() { return correlationId; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
