package com.payment.app.bean;

import java.time.LocalDateTime;

public record PaymentStatusHistory(
        String id,
        String paymentId,
        PaymentStatus fromStatus,
        PaymentStatus toStatus,
        String reason,
        LocalDateTime createdAt
) {
}
