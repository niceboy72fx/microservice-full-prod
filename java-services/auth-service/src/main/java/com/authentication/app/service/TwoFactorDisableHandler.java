package com.authentication.app.service;

import com.authentication.app.command.TwoFactorDisableCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.authen.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorDisableHandler implements CommandHandler<TwoFactorDisableCommand, Void> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public TwoFactorDisableHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<TwoFactorDisableCommand> commandType() {
        return TwoFactorDisableCommand.class;
    }

    @Override
    public Void handle(TwoFactorDisableCommand command) {
        authService.disableTwoFactor(command.username(), command.code());
        return null;
    }
}
