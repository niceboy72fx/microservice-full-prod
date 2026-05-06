package com.account.app.command.account.handler;

import com.account.app.command.account.command.GetAccountDetailCommand;
import com.account.app.command.core.CommandHandler;
import com.account.app.command.core.CommandRegistry;
import com.account.app.domain.service.account.AccountDomainService;
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
