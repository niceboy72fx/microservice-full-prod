package com.account.app.command.account.command;

import com.account.app.command.core.Command;
import com.account.app.dto.response.account.AccountDetailResponse;

public record GetAccountDetailCommand(String accountId) implements Command<AccountDetailResponse> {}
