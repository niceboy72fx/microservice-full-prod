package com.authentication.app.bean;

public record ForgotPasswordResponse(
        String message,
        String correlationId
) {
}
