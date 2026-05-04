package com.risk.app.config;

import java.util.List;

public record KafkaConfig(
        List<String> brokers,
        String consumerGroup
) {}
