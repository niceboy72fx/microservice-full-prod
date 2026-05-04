package com.ledger.app.config;

import java.util.Arrays;
import java.util.List;

public final class ServiceConfigLoader {
    private ServiceConfigLoader() {}

    public static ServiceConfig load() {
        DatabaseConfig database = new DatabaseConfig(
                get("DB_URL", "jdbc:oracle:thin:@//localhost:1521/XEPDB1"),
                get("DB_USER", "app"),
                get("DB_PASSWORD", "app")
        );

        List<String> brokers = Arrays.stream(get("KAFKA_BROKERS", "localhost:9092").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        KafkaConfig kafka = new KafkaConfig(
                brokers,
                get("KAFKA_CONSUMER_GROUP", "ledger-service-consumer")
        );

        RedisConfig redis = new RedisConfig(
                get("REDIS_HOST", "localhost"),
                getInt("REDIS_PORT", 6379)
        );

        GrpcConfig grpc = new GrpcConfig(
                get("GRPC_HOST", "localhost"),
                getInt("GRPC_PORT", 50051)
        );

        return new ServiceConfig(database, kafka, redis, grpc);
    }

    private static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;
        return defaultValue;
    }

    private static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)).trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
