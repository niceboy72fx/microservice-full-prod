package com.authentication.app.service;

import com.authentication.app.command.TwoFactorEnableCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.authen.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorEnableHandler implements CommandHandler<TwoFactorEnableCommand, Void> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public TwoFactorEnableHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<TwoFactorEnableCommand> commandType() {
        return TwoFactorEnableCommand.class;
    }

    @Override
    public Void handle(TwoFactorEnableCommand command) {
        authService.enableTwoFactor(command.username(), command.code());
        return null;
    }
}
