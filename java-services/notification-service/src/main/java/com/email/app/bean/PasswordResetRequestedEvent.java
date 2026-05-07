package com.email.app.bean;

public record PasswordResetRequestedEvent(
        String eventId,
        String correlationId,
        String eventType,
        String userId,
        String email,
        String resetToken
) {
}
