package com.account.app.persistence.entity.account;

public record AccountEntity(
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
