package com.commonservice.common.observability;

import com.commonservice.common.config.ElasticConfigRegistry;
import java.util.ArrayList;
import java.util.List;

public final class LoggerFactory {
    private static final String DEFAULT_BOOTSTRAP = "localhost:9092";
    private static final String DEFAULT_LOG_TOPIC = "platform.logs";

    private LoggerFactory() {
    }

    public static Logger create(String serviceName) {
        String configured = ElasticConfigRegistry.logging().level();
        LogLevel level = parseLevel(configured);
        Logger consoleLogger = new JsonConsoleLogger(serviceName, level);

        String bootstrapServers = readEnv("KAFKA_BOOTSTRAP_SERVERS", DEFAULT_BOOTSTRAP);
        String logTopic = readEnv("COMMON_LOG_TOPIC", DEFAULT_LOG_TOPIC);

        Logger kafkaLogger = new KafkaLogLogger(serviceName, level, bootstrapServers, logTopic);
        List<Logger> delegates = new ArrayList<>();
        delegates.add(consoleLogger);
        delegates.add(kafkaLogger);
        return new CompositeLogger(delegates);
    }

    private static String parseOrDefault(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String readEnv(String key, String defaultValue) {
        return parseOrDefault(System.getenv(key), defaultValue);
    }

    private static LogLevel parseLevel(String level) {
        try {
            return LogLevel.valueOf(level == null ? "INFO" : level.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LogLevel.INFO;
        }
    }
}
