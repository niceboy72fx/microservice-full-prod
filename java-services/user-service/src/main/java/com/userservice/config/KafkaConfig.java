package com.userservice.config;

import java.util.List;

public record KafkaConfig(List<String> brokers, String consumerGroup) {}
