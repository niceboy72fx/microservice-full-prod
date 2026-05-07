package com.account.app.command;

import com.account.app.command.account.command.CreateAccountCommand;
import com.account.app.command.account.command.EkycAccountCommand;
import com.account.app.command.account.command.GetAccountDetailCommand;
import com.account.app.command.account.command.ListAccountsCommand;
import com.account.app.command.account.command.SoftDeleteAccountCommand;
import com.account.app.dto.request.account.CreateAccountRequest;
import com.account.app.dto.request.account.EkycRequest;
import org.springframework.stereotype.Component;

@Component
public class AccountCommandMapper {

    public CreateAccountCommand toCreateCommand(CreateAccountRequest request) {
        return new CreateAccountCommand(request.getUserId(), request.getGender(), request.getBankName(), request.getAccountNumber(), request.getAccountName());
    }

    public GetAccountDetailCommand toDetailCommand(String accountId) {
        return new GetAccountDetailCommand(accountId);
    }

    public ListAccountsCommand toListCommand(String keyword, String status, String ekycStatus, Integer page, Integer size) {
        return new ListAccountsCommand(keyword, status, ekycStatus, page == null ? 1 : page, size == null ? 20 : size);
    }

    public SoftDeleteAccountCommand toSoftDeleteCommand(String accountId) {
        return new SoftDeleteAccountCommand(accountId);
    }

    public EkycAccountCommand toEkycCommand(String accountId, EkycRequest request) {
        return new EkycAccountCommand(accountId, request.getStatus(), request.getSelfieUrl());
    }
}
