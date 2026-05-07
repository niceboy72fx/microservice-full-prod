package com.account.app.command;

import com.account.app.command.Command;
import com.account.app.dto.response.account.AccountDetailResponse;

public record GetAccountDetailCommand(String accountId) implements Command<AccountDetailResponse> {}
