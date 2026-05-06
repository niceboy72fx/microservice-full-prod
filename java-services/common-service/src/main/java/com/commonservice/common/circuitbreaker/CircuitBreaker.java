package com.commonservice.common.circuitbreaker;

public interface CircuitBreaker {

    String name();

    <T> T call(CheckedSupplier<T> supplier);

    void run(CheckedRunnable runnable);

    State state();

    enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }
}
