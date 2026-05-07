package com.payment.app.service;

import com.payment.app.bean.payment.PaymentStatus;

public record PaymentProviderResult(
        PaymentStatus status,
        String providerTransactionId,
        String failureReason
) {
}
