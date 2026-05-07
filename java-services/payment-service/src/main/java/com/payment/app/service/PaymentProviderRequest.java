package com.payment.app.service;

import com.payment.app.bean.payment.PaymentMethod;
import com.payment.app.bean.payment.PaymentType;
import java.math.BigDecimal;

public record PaymentProviderRequest(
        String paymentId,
        String userId,
        PaymentType type,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        String correlationId
) {
}
