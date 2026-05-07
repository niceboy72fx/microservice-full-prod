package com.payment.app.service;

import com.payment.app.bean.payment.PaymentProviderType;

public interface PaymentProvider {

    PaymentProviderType providerType();

    PaymentProviderResult process(PaymentProviderRequest request);
}
