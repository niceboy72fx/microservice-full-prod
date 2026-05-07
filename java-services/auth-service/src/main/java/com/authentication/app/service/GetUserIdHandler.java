package com.authentication.app.service;

import com.authentication.app.command.GetUserIdCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.bean.UserIdResponse;
import com.authentication.app.authen.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GetUserIdHandler implements CommandHandler<GetUserIdCommand, UserIdResponse> {

    private final CommandRegistry commandRegistry;
    private final AuthService authService;

    public GetUserIdHandler(CommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<GetUserIdCommand> commandType() {
        return GetUserIdCommand.class;
    }

    @Override
    public UserIdResponse handle(GetUserIdCommand command) {
        return authService.getUserIdFromToken(command.token());
    }
}
