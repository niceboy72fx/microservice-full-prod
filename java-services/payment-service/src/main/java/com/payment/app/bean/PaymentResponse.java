package com.payment.app.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentId,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        PaymentType type,
        PaymentMethod method,
        PaymentProviderType provider,
        String providerTransactionId,
        String failureReason,
        LocalDateTime createdAt
) {
}
