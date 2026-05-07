package com.commonservice.common.dto;

import com.commonservice.common.enumtype.SettlementStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SettlementBatchDto(
        String batchId,
        List<String> paymentIds,
        BigDecimal totalAmount,
        SettlementStatus status,
        Instant createdAt
) {}
