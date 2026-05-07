package com.payment.app.command;

import com.payment.app.bean.payment.PaymentResponse;
import com.payment.app.bean.payment.PaymentMethod;
import com.payment.app.bean.payment.PaymentType;
import java.math.BigDecimal;

public class CreatePaymentCommand implements Command<PaymentResponse> {

    private final String userId;
    private final PaymentType type;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentMethod method;
    private final String idempotencyKey;
    private final String idempotencyKeyHeader;
    private final String correlationIdHeader;

    public CreatePaymentCommand(String userId, PaymentType type, BigDecimal amount, String currency,
                                PaymentMethod method, String idempotencyKey, String idempotencyKeyHeader,
                                String correlationIdHeader) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.idempotencyKey = idempotencyKey;
        this.idempotencyKeyHeader = idempotencyKeyHeader;
        this.correlationIdHeader = correlationIdHeader;
    }

    public String getUserId() { return userId; }
    public PaymentType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentMethod getMethod() { return method; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getIdempotencyKeyHeader() { return idempotencyKeyHeader; }
    public String getCorrelationIdHeader() { return correlationIdHeader; }
}
