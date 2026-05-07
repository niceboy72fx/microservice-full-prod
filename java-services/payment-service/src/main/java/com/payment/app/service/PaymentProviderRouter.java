package com.payment.app.service;

import com.payment.app.common.exception.BusinessException;
import com.payment.app.bean.payment.PaymentMethod;
import com.payment.app.bean.payment.PaymentProviderType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderRouter {

    private final Map<PaymentProviderType, PaymentProvider> providersByType;

    public PaymentProviderRouter(List<PaymentProvider> providers) {
        this.providersByType = providers.stream().collect(Collectors.toMap(PaymentProvider::providerType, Function.identity()));
    }

    public PaymentProvider resolve(PaymentMethod method, PaymentProviderType providerType) {
        PaymentProviderType selected = determineProviderType(method);
        PaymentProvider byMethod = providersByType.get(selected);
        if (byMethod != null) {
            return byMethod;
        }

        PaymentProvider byType = providersByType.get(providerType);
        if (byType != null) {
            return byType;
        }

        throw new BusinessException("PAYMENT_PROVIDER_NOT_FOUND", "No provider configured for method " + method + " and type " + providerType);
    }

    public PaymentProviderType determineProviderType(PaymentMethod method) {
        // Phase 1: route all methods to fake provider, keep extensible for future mapping.
        return PaymentProviderType.FAKE;
    }
}
