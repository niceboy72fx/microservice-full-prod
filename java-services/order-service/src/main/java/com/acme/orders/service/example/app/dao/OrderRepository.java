package com.acme.orders.service.example.app.dao;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
}
