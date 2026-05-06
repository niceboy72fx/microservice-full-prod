package com.authentication.app.command.auth.handler;

import com.authentication.app.command.auth.command.GetUserIdCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.dto.response.auth.UserIdResponse;
import com.authentication.app.security.AuthService;
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
