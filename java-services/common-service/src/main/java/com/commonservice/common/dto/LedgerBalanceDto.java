package com.commonservice.common.dto;

import com.commonservice.common.enumtype.CurrencyType;
import java.math.BigDecimal;

public record LedgerBalanceDto(
        String accountId,
        CurrencyType currency,
        BigDecimal availableBalance,
        BigDecimal reservedBalance,
        BigDecimal totalBalance
) {}
