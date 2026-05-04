package com.userservice.config;

import java.util.Arrays;
import java.util.List;

public final class ServiceConfigLoader {
    private ServiceConfigLoader() {}

    public static ServiceConfig load() {
        DatabaseConfig database = new DatabaseConfig(get("DB_URL", "jdbc:oracle:thin:@//localhost:1521/XEPDB1"), get("DB_USER", "app"), get("DB_PASSWORD", "app"));
        List<String> brokers = Arrays.stream(get("KAFKA_BROKERS", "localhost:9092").split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        KafkaConfig kafka = new KafkaConfig(brokers, get("KAFKA_CONSUMER_GROUP", "user-service-consumer"));
        RedisConfig redis = new RedisConfig(get("REDIS_HOST", "localhost"), getInt("REDIS_PORT", 6379));
        GrpcConfig grpc = new GrpcConfig(get("GRPC_HOST", "localhost"), getInt("GRPC_PORT", 50051));
        return new ServiceConfig(database, kafka, redis, grpc);
    }

    private static String get(String key, String def) {
        String s = System.getProperty(key);
        if (s != null && !s.isBlank()) return s;
        String e = System.getenv(key);
        if (e != null && !e.isBlank()) return e;
        return def;
    }

    private static int getInt(String key, int def) {
        try { return Integer.parseInt(get(key, String.valueOf(def)).trim()); } catch (NumberFormatException ex) { return def; }
    }
}
