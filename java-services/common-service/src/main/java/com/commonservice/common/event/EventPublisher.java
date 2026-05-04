package com.commonservice.common.event;

public interface EventPublisher {
    <T> void publish(EventEnvelope<T> event);
}
