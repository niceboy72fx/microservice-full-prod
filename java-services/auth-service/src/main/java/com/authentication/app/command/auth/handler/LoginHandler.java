package com.authentication.app.command.auth.handler;

import com.authentication.app.command.auth.command.LoginCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.dto.response.auth.LoginResponse;
import com.authentication.app.security.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LoginHandler implements CommandHandler<LoginCommand, LoginResponse> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public LoginHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<LoginCommand> commandType() {
        return LoginCommand.class;
    }

    @Override
    public LoginResponse handle(LoginCommand command) {
        return authService.login(command.username(), command.password(), command.otpCode(), command.ipAddress());
    }
}
