package com.authentication.app.command.auth.handler;

import com.authentication.app.command.auth.command.LogoutCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.security.AuthService;
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
