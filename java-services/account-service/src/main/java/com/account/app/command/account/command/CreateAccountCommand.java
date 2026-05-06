package com.account.app.command.account.command;

import com.account.app.command.core.Command;
import com.account.app.dto.response.account.AccountDetailResponse;

public record CreateAccountCommand(String userId, String gender, String bankName, String accountNumber, String accountName)
        implements Command<AccountDetailResponse> {}
