package com.account.app.command;

import com.account.app.command.account.command.EkycAccountCommand;
import com.account.app.command.CommandHandler;
import com.account.app.command.CommandRegistry;
import com.account.app.service.account.AccountDomainService;
import com.account.app.dto.response.account.AccountDetailResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class EkycAccountHandler implements CommandHandler<EkycAccountCommand, AccountDetailResponse> {

    private final CommandRegistry commandRegistry;
    private final AccountDomainService accountDomainService;

    public EkycAccountHandler(CommandRegistry commandRegistry, AccountDomainService accountDomainService) {
        this.commandRegistry = commandRegistry;
        this.accountDomainService = accountDomainService;
    }

    @PostConstruct
    public void register() { commandRegistry.register(this); }

    @Override
    public Class<EkycAccountCommand> commandType() { return EkycAccountCommand.class; }

    @Override
    public AccountDetailResponse handle(EkycAccountCommand command) {
        return accountDomainService.ekyc(command.accountId(), command.status(), command.selfieUrl());
    }
}
