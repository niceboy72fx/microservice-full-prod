package com.commonservice.common.observability;

import java.util.List;
import java.util.Map;

public final class CompositeLogger implements Logger {
    private final List<Logger> delegates;

    public CompositeLogger(List<Logger> delegates) {
        this.delegates = List.copyOf(delegates);
    }

    @Override
    public void log(LogLevel level, String message) {
        for (Logger delegate : delegates) {
            delegate.log(level, message);
        }
    }

    @Override
    public void log(LogLevel level, String message, Map<String, Object> fields) {
        for (Logger delegate : delegates) {
            delegate.log(level, message, fields);
        }
    }
}
