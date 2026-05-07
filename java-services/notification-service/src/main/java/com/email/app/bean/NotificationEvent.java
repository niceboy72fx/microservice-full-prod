package com.email.app.bean;

public record NotificationEvent(
        String eventId,
        String correlationId,
        String userId,
        String recipient,
        String subject,
        String body,
        int retryCount
) {
    public NotificationEvent withRetryCount(int nextRetryCount) {
        return new NotificationEvent(eventId, correlationId, userId, recipient, subject, body, nextRetryCount);
    }
}
