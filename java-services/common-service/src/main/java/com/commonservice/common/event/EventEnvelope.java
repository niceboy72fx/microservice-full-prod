package com.commonservice.common.event;

import java.time.Instant;
import java.util.Objects;

public final class EventEnvelope<T> {
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final T payload;

    public EventEnvelope(String eventId, String eventType, Instant occurredAt, T payload) {
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        this.payload = Objects.requireNonNull(payload, "payload must not be null");
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "EventEnvelope{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", occurredAt=" + occurredAt +
                ", payload=" + payload +
                '}';
    }
}
