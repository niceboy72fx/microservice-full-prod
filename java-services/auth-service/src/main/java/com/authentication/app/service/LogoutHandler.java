package com.authentication.app.service;

import com.authentication.app.command.LogoutCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.authen.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandler implements CommandHandler<LogoutCommand, Void> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public LogoutHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<LogoutCommand> commandType() {
        return LogoutCommand.class;
    }

    @Override
    public Void handle(LogoutCommand command) {
        authService.logout(command.refreshToken());
        return null;
    }
}
