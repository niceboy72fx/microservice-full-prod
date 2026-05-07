package com.account.app.command;

import com.account.app.command.Command;
import com.account.app.dto.response.account.AccountDetailResponse;

public record CreateAccountCommand(String userId, String gender, String bankName, String accountNumber, String accountName)
        implements Command<AccountDetailResponse> {}
