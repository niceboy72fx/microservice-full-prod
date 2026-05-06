package com.commonservice.common.circuitbreaker;

public interface CircuitBreakerRegistry {

    CircuitBreaker get(String name);

    <T> T call(String name, CheckedSupplier<T> supplier);

    void run(String name, CheckedRunnable runnable);
}
