package com.acme.orders.rest;

import java.math.BigDecimal;

public class OrderRequest {
    private String customerId;
    private BigDecimal amount;
    private String currency;

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
