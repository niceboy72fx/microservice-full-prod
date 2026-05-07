package com.commonservice.common.dto;

import java.time.Instant;

public record AuditMetadataDto(
        String actor,
        String action,
        String source,
        String correlationId,
        Instant eventTime
) {}
