package com.commonservice.common.dto;

import com.commonservice.common.enumtype.RiskDecision;

public record RiskDecisionDto(
        String paymentId,
        RiskDecision decision,
        String reason,
        String score
) {}
