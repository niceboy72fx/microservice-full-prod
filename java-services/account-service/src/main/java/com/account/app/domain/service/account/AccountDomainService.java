package com.account.app.domain.service.account;

import com.account.app.common.exception.BusinessException;
import com.account.app.domain.model.account.AccountModel;
import com.account.app.dto.response.account.AccountDetailResponse;
import com.account.app.dto.response.account.PagedAccountsResponse;
import com.account.app.repository.account.AccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountDomainService {

    private final AccountRepository accountRepository;

    public AccountDomainService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDetailResponse create(String userId, String gender, String bankName, String accountNumber, String accountName) {
        String normalizedUserId = normalizeRequired(userId, "userId");
        String normalizedGender = normalizeRequired(gender, "gender").toUpperCase();
        String normalizedBankName = normalizeRequired(bankName, "bankName");
        String normalizedAccountNumber = normalizeRequired(accountNumber, "accountNumber");
        String normalizedAccountName = normalizeRequired(accountName, "accountName");
        if (accountRepository.existsByUserId(normalizedUserId)) {
            throw new BusinessException("ACCOUNT_USER_EXISTS", "User already has an account");
        }
        String accountId = accountRepository.create(normalizedUserId, normalizedGender, normalizedBankName, normalizedAccountNumber, normalizedAccountName);
        return detail(accountId);
    }

    public AccountDetailResponse detail(String accountId) {
        AccountModel model = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("ACCOUNT_NOT_FOUND", "Account not found"));
        return toResponse(model);
    }

    public PagedAccountsResponse list(String keyword, String status, String ekycStatus, int page, int size) {
        int safePage = page < 1 ? 1 : page;
        int safeSize = size < 1 ? 20 : Math.min(size, 100);
        List<AccountDetailResponse> items = accountRepository.list(keyword, status, ekycStatus, safePage, safeSize)
                .stream().map(this::toResponse).toList();
        long total = accountRepository.count(keyword, status, ekycStatus);
        return new PagedAccountsResponse(safePage, safeSize, total, items);
    }

    public void softDelete(String accountId) {
        detail(accountId);
        accountRepository.softDelete(accountId);
    }

    public AccountDetailResponse ekyc(String accountId, String status, String selfieUrl) {
        AccountDetailResponse current = detail(accountId);
        String normalizedStatus = normalizeRequired(status, "status").toUpperCase();
        String normalizedSelfieUrl = normalizeRequired(selfieUrl, "selfieUrl");
        boolean updated = accountRepository.updateEkyc(current.userId(), normalizedStatus, normalizedSelfieUrl);
        if (!updated) {
            throw new BusinessException("EKYC_NOT_FOUND", "eKYC record not found");
        }
        return detail(accountId);
    }

    private String normalizeRequired(String value, String fieldName) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new BusinessException("INVALID_INPUT", fieldName + " is required");
        }
        return normalized;
    }

    private AccountDetailResponse toResponse(AccountModel model) {
        return new AccountDetailResponse(model.id(), model.userId(), model.gender(), model.status(), model.ekycStatus(), model.selfieUrl(), model.bankName(), model.accountNumber(), model.accountName());
    }
}
