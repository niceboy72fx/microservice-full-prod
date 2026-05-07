package com.authentication.app.service;

import com.authentication.app.command.ForgotPasswordCommand;
import com.authentication.app.command.CommandHandler;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.bean.ForgotPasswordResponse;
import com.authentication.app.authen.passwordreset.ForgotPasswordService;
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
