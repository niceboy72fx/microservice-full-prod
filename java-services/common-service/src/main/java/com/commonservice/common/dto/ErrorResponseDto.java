package com.commonservice.common.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String code,
        String message,
        String correlationId,
        Instant timestamp
) {}
