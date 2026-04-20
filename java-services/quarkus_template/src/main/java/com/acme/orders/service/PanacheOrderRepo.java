package com.acme.orders.service;

import com.acme.orders.domain.Order;
import com.acme.orders.domain.OrderRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PanacheOrderRepo implements OrderRepository, PanacheRepositoryBase<Order, String> {

    @Override
    public void save(Order order) {
        persist(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return findByIdOptional(id);
    }
}
