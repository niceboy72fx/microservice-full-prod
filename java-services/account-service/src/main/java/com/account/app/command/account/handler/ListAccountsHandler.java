package com.account.app.command;

import com.account.app.command.account.command.ListAccountsCommand;
import com.account.app.command.CommandHandler;
import com.account.app.command.CommandRegistry;
import com.account.app.service.account.AccountDomainService;
import com.account.app.dto.response.account.PagedAccountsResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ListAccountsHandler implements CommandHandler<ListAccountsCommand, PagedAccountsResponse> {

    private final CommandRegistry commandRegistry;
    private final AccountDomainService accountDomainService;

    public ListAccountsHandler(CommandRegistry commandRegistry, AccountDomainService accountDomainService) {
        this.commandRegistry = commandRegistry;
        this.accountDomainService = accountDomainService;
    }

    @PostConstruct
    public void register() { commandRegistry.register(this); }

    @Override
    public Class<ListAccountsCommand> commandType() { return ListAccountsCommand.class; }

    @Override
    public PagedAccountsResponse handle(ListAccountsCommand command) {
        return accountDomainService.list(command.keyword(), command.status(), command.ekycStatus(), command.page(), command.size());
    }
}
