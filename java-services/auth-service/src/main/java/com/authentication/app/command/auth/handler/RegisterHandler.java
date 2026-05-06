package com.authentication.app.command.auth.handler;

import com.authentication.app.command.auth.command.RegisterCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.dto.response.auth.LoginResponse;
import com.authentication.app.security.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RegisterHandler implements CommandHandler<RegisterCommand, LoginResponse> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public RegisterHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<RegisterCommand> commandType() {
        return RegisterCommand.class;
    }

    @Override
    public LoginResponse handle(RegisterCommand command) {
        return authService.register(command.username(), command.password());
    }
}
