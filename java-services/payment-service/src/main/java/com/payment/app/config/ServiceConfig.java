package com.payment.app.config;

public record ServiceConfig(
        DatabaseConfig database,
        KafkaConfig kafka,
        RedisConfig redis,
        GrpcConfig grpc
) {}
