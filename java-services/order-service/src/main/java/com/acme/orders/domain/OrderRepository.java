package com.acme.orders.domain;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
}
