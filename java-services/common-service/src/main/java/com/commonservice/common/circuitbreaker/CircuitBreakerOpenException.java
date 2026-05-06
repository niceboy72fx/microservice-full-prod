package com.commonservice.common.circuitbreaker;

public class CircuitBreakerOpenException extends RuntimeException {
    public CircuitBreakerOpenException(String breakerName) {
        super("Circuit breaker is open: " + breakerName);
    }
}
