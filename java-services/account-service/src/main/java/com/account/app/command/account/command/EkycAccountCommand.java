package com.account.app.command;

import com.account.app.command.Command;
import com.account.app.dto.response.account.AccountDetailResponse;

public record EkycAccountCommand(String accountId, String status, String selfieUrl)
        implements Command<AccountDetailResponse> {}
