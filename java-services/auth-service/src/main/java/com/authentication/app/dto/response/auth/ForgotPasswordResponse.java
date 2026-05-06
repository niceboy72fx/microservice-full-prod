package com.authentication.app.dto.response.auth;

public record ForgotPasswordResponse(
        String message,
        String correlationId
) {
}
