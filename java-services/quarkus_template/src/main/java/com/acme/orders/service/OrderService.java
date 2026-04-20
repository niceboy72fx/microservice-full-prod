package com.acme.orders.service;

import com.acme.orders.domain.Money;
import com.acme.orders.domain.Order;
import com.acme.orders.domain.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Transactional
    public Order createOrder(String customerId, BigDecimal amount, String currency) {
        Money totalAmount = new Money(amount, currency);
        Order order = new Order(customerId, totalAmount);
        orderRepository.save(order);
        // Here you could add logic to publish to Kafka, cache in Redis, etc.
        return order;
    }
}
