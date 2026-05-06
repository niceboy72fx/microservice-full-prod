package com.authentication.app.command.auth.handler.twofactor;

import com.authentication.app.command.auth.command.twofactor.TwoFactorSetupCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.dto.response.auth.twofactor.TwoFactorSetupResponse;
import com.authentication.app.security.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorSetupHandler implements CommandHandler<TwoFactorSetupCommand, TwoFactorSetupResponse> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public TwoFactorSetupHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<TwoFactorSetupCommand> commandType() {
        return TwoFactorSetupCommand.class;
    }

    @Override
    public TwoFactorSetupResponse handle(TwoFactorSetupCommand command) {
        return authService.setupTwoFactor(command.username());
    }
}
