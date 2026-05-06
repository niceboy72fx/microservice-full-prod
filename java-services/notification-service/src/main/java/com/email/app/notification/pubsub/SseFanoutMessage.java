package com.email.app.notification.pubsub;

public record SseFanoutMessage(
        String userId,
        String eventName,
        String eventId,
        String correlationId,
        String status,
        String subject,
        String message
) {
}
