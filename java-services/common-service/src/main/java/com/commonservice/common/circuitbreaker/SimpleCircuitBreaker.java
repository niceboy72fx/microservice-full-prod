package com.commonservice.common.circuitbreaker;

import java.time.Duration;
import java.time.Instant;

public class SimpleCircuitBreaker implements CircuitBreaker {

    private final String name;
    private final int failureThreshold;
    private final int minimumCalls;
    private final int halfOpenMaxCalls;
    private final Duration openDuration;

    private State state = State.CLOSED;
    private int totalCalls;
    private int failedCalls;
    private int halfOpenInFlight;
    private Instant openUntil = Instant.EPOCH;

    public SimpleCircuitBreaker(String name, CircuitBreakerConfigProperties.Rule rule) {
        this.name = name;
        this.failureThreshold = Math.max(1, rule.getFailureThreshold());
        this.minimumCalls = Math.max(1, rule.getMinimumCalls());
        this.halfOpenMaxCalls = Math.max(1, rule.getHalfOpenMaxCalls());
        this.openDuration = rule.getOpenDuration() == null ? Duration.ofSeconds(30) : rule.getOpenDuration();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public <T> T call(CheckedSupplier<T> supplier) {
        acquirePermission();
        try {
            T response = supplier.get();
            onSuccess();
            return response;
        } catch (Exception exception) {
            onFailure();
            throw unchecked(exception);
        }
    }

    @Override
    public void run(CheckedRunnable runnable) {
        call(() -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public synchronized State state() {
        if (state == State.OPEN && Instant.now().isAfter(openUntil)) {
            moveToHalfOpen();
        }
        return state;
    }

    private synchronized void acquirePermission() {
        if (state == State.OPEN) {
            if (Instant.now().isAfter(openUntil)) {
                moveToHalfOpen();
            } else {
                throw new CircuitBreakerOpenException(name);
            }
        }

        if (state == State.HALF_OPEN) {
            if (halfOpenInFlight >= halfOpenMaxCalls) {
                throw new CircuitBreakerOpenException(name);
            }
            halfOpenInFlight++;
        }
    }

    private synchronized void onSuccess() {
        if (state == State.HALF_OPEN) {
            halfOpenInFlight = Math.max(0, halfOpenInFlight - 1);
            state = State.CLOSED;
            resetCounters();
            return;
        }
        totalCalls++;
    }

    private synchronized void onFailure() {
        if (state == State.HALF_OPEN) {
            halfOpenInFlight = Math.max(0, halfOpenInFlight - 1);
            moveToOpen();
            return;
        }

        totalCalls++;
        failedCalls++;
        if (totalCalls >= minimumCalls && failedCalls >= failureThreshold) {
            moveToOpen();
        }
    }

    private void moveToOpen() {
        state = State.OPEN;
        openUntil = Instant.now().plus(openDuration);
    }

    private void moveToHalfOpen() {
        state = State.HALF_OPEN;
        halfOpenInFlight = 0;
    }

    private void resetCounters() {
        totalCalls = 0;
        failedCalls = 0;
    }

    private RuntimeException unchecked(Exception exception) {
        if (exception instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        return new RuntimeException(exception);
    }
}
