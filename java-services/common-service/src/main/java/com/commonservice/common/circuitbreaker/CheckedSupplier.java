package com.commonservice.common.circuitbreaker;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
