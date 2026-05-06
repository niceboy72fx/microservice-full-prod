package com.commonservice.common.circuitbreaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCircuitBreakerRegistry implements CircuitBreakerRegistry {

    private final CircuitBreakerConfigProperties properties;
    private final Map<String, CircuitBreaker> breakers = new ConcurrentHashMap<>();

    public InMemoryCircuitBreakerRegistry(CircuitBreakerConfigProperties properties) {
        this.properties = properties;
    }

    @Override
    public CircuitBreaker get(String name) {
        return breakers.computeIfAbsent(name, this::create);
    }

    @Override
    public <T> T call(String name, CheckedSupplier<T> supplier) {
        return get(name).call(supplier);
    }

    @Override
    public void run(String name, CheckedRunnable runnable) {
        get(name).run(runnable);
    }

    private CircuitBreaker create(String name) {
        CircuitBreakerConfigProperties.Rule rule = properties.getInstances().getOrDefault(name, properties.getDefaults());
        return new SimpleCircuitBreaker(name, rule);
    }
}
