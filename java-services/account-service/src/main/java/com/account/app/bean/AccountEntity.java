package com.account.app.bean;

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
