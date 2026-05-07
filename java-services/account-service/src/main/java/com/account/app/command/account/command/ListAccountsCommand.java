package com.account.app.command;

import com.account.app.command.Command;
import com.account.app.dto.response.account.PagedAccountsResponse;

public record ListAccountsCommand(String keyword, String status, String ekycStatus, int page, int size)
        implements Command<PagedAccountsResponse> {}
