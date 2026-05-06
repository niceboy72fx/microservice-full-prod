package com.email.app.notification.sse;

public record SseNotificationPayload(
        String eventId,
        String correlationId,
        String userId,
        String status,
        String subject,
        String message
) {
}
