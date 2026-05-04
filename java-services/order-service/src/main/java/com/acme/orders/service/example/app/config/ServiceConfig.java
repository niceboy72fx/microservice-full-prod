package com.example.app.config;

public record ServiceConfig(DatabaseConfig database, KafkaConfig kafka, RedisConfig redis, GrpcConfig grpc) {}
