package com.acme.orders.service.example.app.config;

import java.util.List;

public record KafkaConfig(List<String> brokers, String consumerGroup) {}
