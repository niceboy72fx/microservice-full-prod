package com.authentication.app.command.auth.handler.twofactor;

import com.authentication.app.command.auth.command.twofactor.TwoFactorDisableCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.security.AuthService;
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
