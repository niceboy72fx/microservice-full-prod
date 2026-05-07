package com.payment.app.service;

import com.payment.app.bean.payment.PaymentProviderType;
import com.payment.app.bean.payment.PaymentStatus;
import com.payment.app.service.PaymentProvider;
import com.payment.app.service.PaymentProviderRequest;
import com.payment.app.service.PaymentProviderResult;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FakePaymentProvider implements PaymentProvider {

    @Override
    public PaymentProviderType providerType() {
        return PaymentProviderType.FAKE;
    }

    @Override
    public PaymentProviderResult process(PaymentProviderRequest request) {
        BigDecimal amount = request.amount();
        String providerTransactionId = "FAKE-" + UUID.randomUUID();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentProviderResult(PaymentStatus.FAILED, providerTransactionId, "Invalid amount");
        }
        if (amount.compareTo(BigDecimal.valueOf(999999)) == 0) {
            return new PaymentProviderResult(PaymentStatus.UNKNOWN, providerTransactionId, "Temporary unknown from provider");
        }
        return new PaymentProviderResult(PaymentStatus.SUCCESS, providerTransactionId, null);
    }
}
