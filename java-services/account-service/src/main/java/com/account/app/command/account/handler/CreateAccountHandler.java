package com.account.app.command;

import com.account.app.command.account.command.CreateAccountCommand;
import com.account.app.command.CommandHandler;
import com.account.app.command.CommandRegistry;
import com.account.app.service.account.AccountDomainService;
import com.account.app.dto.response.account.AccountDetailResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountHandler implements CommandHandler<CreateAccountCommand, AccountDetailResponse> {

    private final CommandRegistry commandRegistry;
    private final AccountDomainService accountDomainService;

    public CreateAccountHandler(CommandRegistry commandRegistry, AccountDomainService accountDomainService) {
        this.commandRegistry = commandRegistry;
        this.accountDomainService = accountDomainService;
    }

    @PostConstruct
    public void register() { commandRegistry.register(this); }

    @Override
    public Class<CreateAccountCommand> commandType() { return CreateAccountCommand.class; }

    @Override
    public AccountDetailResponse handle(CreateAccountCommand command) {
        return accountDomainService.create(command.userId(), command.gender(), command.bankName(), command.accountNumber(), command.accountName());
    }
}
