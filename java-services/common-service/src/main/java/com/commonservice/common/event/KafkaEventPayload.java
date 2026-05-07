package com.commonservice.common.event;

import java.time.Instant;
import java.util.Map;

public record KafkaEventPayload(
        KafkaEventType eventType,
        String aggregateId,
        String correlationId,
        Instant occurredAt,
        Map<String, Object> payload
) {}
