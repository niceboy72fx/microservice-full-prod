package com.commonservice.common.dto;

import com.commonservice.common.enumtype.CurrencyType;
import java.math.BigDecimal;

public record RiskCheckRequestDto(
        String paymentId,
        String accountId,
        BigDecimal amount,
        CurrencyType currency,
        String channel,
        String ipAddress
) {}
