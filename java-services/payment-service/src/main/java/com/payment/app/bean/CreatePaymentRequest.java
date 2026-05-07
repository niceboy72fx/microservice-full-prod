package com.payment.app.bean;
import java.math.BigDecimal;

public class CreatePaymentRequest {

    private String userId;
    private PaymentType type;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod method;
    private String idempotencyKey;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public PaymentType getType() { return type; }
    public void setType(PaymentType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
