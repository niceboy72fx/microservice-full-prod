package com.email.app.notification.model;

public record PasswordResetRequestedEvent(
        String eventId,
        String correlationId,
        String eventType,
        String userId,
        String email,
        String resetToken
) {
}
