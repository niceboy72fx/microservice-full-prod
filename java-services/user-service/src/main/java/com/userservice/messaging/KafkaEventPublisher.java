package com.userservice.messaging;

import com.commonservice.common.event.EventEnvelope;
import com.commonservice.common.event.EventPublisher;
import com.commonservice.common.json.JsonUtils;

public final class KafkaEventPublisher implements EventPublisher {
    @Override
    public <T> void publish(EventEnvelope<T> event) {
        System.out.println("[KafkaEventPublisher] topic=user-events payload=" + JsonUtils.toJson(event));
    }
}
