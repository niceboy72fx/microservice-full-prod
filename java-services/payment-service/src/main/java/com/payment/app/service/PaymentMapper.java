package com.payment.app.service;

import com.payment.app.bean.payment.Payment;
import com.payment.app.bean.payment.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getType(),
                payment.getMethod(),
                payment.getProvider(),
                payment.getProviderTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}
