package com.email.app.bean;

public record SseNotificationPayload(
        String eventId,
        String correlationId,
        String userId,
        String status,
        String subject,
        String message
) {
}
