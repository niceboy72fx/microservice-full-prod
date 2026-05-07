package com.account.app.dto;

public record AccountDetailResponse(
        String id,
        String userId,
        String gender,
        String status,
        String ekycStatus,
        String selfieUrl,
        String bankName,
        String accountNumber,
        String accountName
) {}
