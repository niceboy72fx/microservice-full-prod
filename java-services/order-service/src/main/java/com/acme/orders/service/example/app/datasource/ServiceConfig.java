package com.acme.orders.service.example.app.datasource;

public record ServiceConfig(DatabaseConfig database, KafkaConfig kafka, RedisConfig redis, GrpcConfig grpc) {}
