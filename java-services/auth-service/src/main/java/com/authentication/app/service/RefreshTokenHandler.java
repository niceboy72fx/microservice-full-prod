package com.authentication.app.service;

import com.authentication.app.command.RefreshTokenCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.bean.LoginResponse;
import com.authentication.app.authen.AuthService;
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
