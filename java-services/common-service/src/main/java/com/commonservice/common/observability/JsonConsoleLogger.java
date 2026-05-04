package com.commonservice.common.observability;

import com.commonservice.common.serialization.JsonUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonConsoleLogger implements Logger {
    private final String serviceName;
    private final LogLevel threshold;

    public JsonConsoleLogger(String serviceName, LogLevel threshold) {
        this.serviceName = serviceName;
        this.threshold = threshold;
    }

    @Override
    public void log(LogLevel level, String message) {
        log(level, message, Map.of());
    }

    @Override
    public void log(LogLevel level, String message, Map<String, Object> fields) {
        if (level.ordinal() < threshold.ordinal()) {
            return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("service", serviceName);
        payload.put("level", level.name());
        payload.put("message", message);
        payload.putAll(fields);

        System.out.println(JsonUtils.toJson(payload));
    }
}
