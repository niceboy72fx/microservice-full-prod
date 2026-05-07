package com.payment.app.command;

import com.payment.app.bean.payment.CreatePaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentCommandMapper {

    public CreatePaymentCommand toCreateCommand(
            CreatePaymentRequest request,
            String idempotencyKeyHeader,
            String correlationIdHeader
    ) {
        return new CreatePaymentCommand(
                request.getUserId(),
                request.getType(),
                request.getAmount(),
                request.getCurrency(),
                request.getMethod(),
                request.getIdempotencyKey(),
                idempotencyKeyHeader,
                correlationIdHeader
        );
    }

    public GetPaymentCommand toGetCommand(String paymentId) {
        return new GetPaymentCommand(paymentId);
    }
}
