package com.commonservice.common.tracing;

import java.util.UUID;

public final class CorrelationIdHolder {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private CorrelationIdHolder() {
    }

    public static String getOrCreate() {
        String current = HOLDER.get();
        if (current == null || current.isBlank()) {
            current = UUID.randomUUID().toString();
            HOLDER.set(current);
        }
        return current;
    }

    public static void set(String correlationId) {
        HOLDER.set(correlationId);
    }

    public static String get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
