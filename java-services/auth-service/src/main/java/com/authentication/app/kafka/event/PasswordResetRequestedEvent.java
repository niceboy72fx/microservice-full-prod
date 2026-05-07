package com.authentication.app.kafka;

public record PasswordResetRequestedEvent(
        String eventId,
        String correlationId,
        String eventType,
        String userId,
        String email,
        String resetToken
) {
}
