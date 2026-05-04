package com.commonservice.common.observability;

import com.commonservice.common.config.ElasticConfigRegistry;

public final class LoggerFactory {
    private LoggerFactory() {
    }

    public static Logger create(String serviceName) {
        String configured = ElasticConfigRegistry.logging().level();
        LogLevel level = parseLevel(configured);
        return new JsonConsoleLogger(serviceName, level);
    }

    private static LogLevel parseLevel(String level) {
        try {
            return LogLevel.valueOf(level == null ? "INFO" : level.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LogLevel.INFO;
        }
    }
}
