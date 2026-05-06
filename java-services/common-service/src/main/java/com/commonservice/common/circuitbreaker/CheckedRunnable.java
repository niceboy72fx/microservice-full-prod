package com.commonservice.common.circuitbreaker;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
