package com.authentication.app.integration.kafka.event;

public record PasswordResetRequestedEvent(
        String eventId,
        String correlationId,
        String eventType,
        String userId,
        String email,
        String resetToken
) {
}
