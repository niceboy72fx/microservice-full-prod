package com.commonservice.common.dto;

import com.commonservice.common.enumtype.PaymentStatus;

public record PaymentResponseDto(
        String paymentId,
        PaymentStatus status,
        String referenceId,
        String message
) {}
