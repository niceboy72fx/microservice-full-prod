package com.acme.orders.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Embedded;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;
    
    private String customerId;
    
    @Embedded
    private Money totalAmount;
    
    private String status;

    protected Order() {}

    public Order(String customerId, Money totalAmount) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = "CREATED";
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Money getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    
    public void markAsPaid() {
        this.status = "PAID";
    }
}
