package com.account.app.domain.model.account;

public record AccountModel(
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
