package com.account.app.command.account.handler;

import com.account.app.command.account.command.SoftDeleteAccountCommand;
import com.account.app.command.core.CommandHandler;
import com.account.app.command.core.CommandRegistry;
import com.account.app.domain.service.account.AccountDomainService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SoftDeleteAccountHandler implements CommandHandler<SoftDeleteAccountCommand, Void> {

    private final CommandRegistry commandRegistry;
    private final AccountDomainService accountDomainService;

    public SoftDeleteAccountHandler(CommandRegistry commandRegistry, AccountDomainService accountDomainService) {
        this.commandRegistry = commandRegistry;
        this.accountDomainService = accountDomainService;
    }

    @PostConstruct
    public void register() { commandRegistry.register(this); }

    @Override
    public Class<SoftDeleteAccountCommand> commandType() { return SoftDeleteAccountCommand.class; }

    @Override
    public Void handle(SoftDeleteAccountCommand command) {
        accountDomainService.softDelete(command.accountId());
        return null;
    }
}
