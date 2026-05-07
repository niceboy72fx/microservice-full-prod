package com.authentication.app.service;

import com.authentication.app.command.LoginCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.bean.LoginResponse;
import com.authentication.app.authen.AuthService;
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
