package com.authentication.app.command.auth.handler.password;

import com.authentication.app.command.auth.command.ForgotPasswordCommand;
import com.authentication.app.command.core.CommandHandler;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.dto.response.auth.ForgotPasswordResponse;
import com.authentication.app.security.passwordreset.ForgotPasswordService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordHandler implements CommandHandler<ForgotPasswordCommand, ForgotPasswordResponse> {

    private final CommandRegistry commandRegistry;
    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordHandler(CommandRegistry commandRegistry, ForgotPasswordService forgotPasswordService) {
        this.commandRegistry = commandRegistry;
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<ForgotPasswordCommand> commandType() {
        return ForgotPasswordCommand.class;
    }

    @Override
    public ForgotPasswordResponse handle(ForgotPasswordCommand command) {
        return forgotPasswordService.requestReset(command.email());
    }
}
