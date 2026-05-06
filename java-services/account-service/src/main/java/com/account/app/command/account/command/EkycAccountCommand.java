package com.account.app.command.account.command;

import com.account.app.command.core.Command;
import com.account.app.dto.response.account.AccountDetailResponse;

public record EkycAccountCommand(String accountId, String status, String selfieUrl)
        implements Command<AccountDetailResponse> {}
