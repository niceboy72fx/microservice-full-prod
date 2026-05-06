package com.commonservice.common.observability;

import java.util.Map;

public interface Logger {
    void log(LogLevel level, String message);

    void log(LogLevel level, String message, Map<String, Object> fields);

    default void info(String message) {
        log(LogLevel.INFO, message);
    }

    default void info(String message, Map<String, Object> fields) {
        log(LogLevel.INFO, message, fields);
    }

    default void warn(String message) {
        log(LogLevel.WARN, message);
    }

    default void warn(String message, Map<String, Object> fields) {
        log(LogLevel.WARN, message, fields);
    }

    default void error(String message) {
        log(LogLevel.ERROR, message);
    }

    default void error(String message, Map<String, Object> fields) {
        log(LogLevel.ERROR, message, fields);
    }

    default void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    default void debug(String message, Map<String, Object> fields) {
        log(LogLevel.DEBUG, message, fields);
    }
}
