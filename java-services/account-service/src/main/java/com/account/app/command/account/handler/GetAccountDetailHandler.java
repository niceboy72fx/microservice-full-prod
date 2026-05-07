package com.account.app.command;

import com.account.app.command.account.command.GetAccountDetailCommand;
import com.account.app.command.CommandHandler;
import com.account.app.command.CommandRegistry;
import com.account.app.service.account.AccountDomainService;
import com.account.app.dto.response.account.AccountDetailResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GetAccountDetailHandler implements CommandHandler<GetAccountDetailCommand, AccountDetailResponse> {

    private final CommandRegistry commandRegistry;
    private final AccountDomainService accountDomainService;

    public GetAccountDetailHandler(CommandRegistry commandRegistry, AccountDomainService accountDomainService) {
        this.commandRegistry = commandRegistry;
        this.accountDomainService = accountDomainService;
    }

    @PostConstruct
    public void register() { commandRegistry.register(this); }

    @Override
    public Class<GetAccountDetailCommand> commandType() { return GetAccountDetailCommand.class; }

    @Override
    public AccountDetailResponse handle(GetAccountDetailCommand command) {
        return accountDomainService.detail(command.accountId());
    }
}
