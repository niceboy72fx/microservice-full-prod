package com.authentication.app.command.auth.handler;

import com.authentication.app.command.auth.command.RefreshTokenCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.dto.response.auth.LoginResponse;
import com.authentication.app.security.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenHandler implements CommandHandler<RefreshTokenCommand, LoginResponse> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public RefreshTokenHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<RefreshTokenCommand> commandType() {
        return RefreshTokenCommand.class;
    }

    @Override
    public LoginResponse handle(RefreshTokenCommand command) {
        return authService.refresh(command.refreshToken());
    }
}
