package com.commonservice.common.dto;

import com.commonservice.common.enumtype.CurrencyType;
import com.commonservice.common.enumtype.PaymentType;
import java.math.BigDecimal;

public record PaymentRequestDto(
        String paymentId,
        String accountId,
        BigDecimal amount,
        CurrencyType currency,
        PaymentType paymentType,
        String description
) {}
