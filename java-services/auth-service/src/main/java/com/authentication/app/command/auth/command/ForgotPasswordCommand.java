package com.authentication.app.command.auth.command;

import com.authentication.app.command.core.Command;
import com.authentication.app.dto.response.auth.ForgotPasswordResponse;

public record ForgotPasswordCommand(String email) implements Command<ForgotPasswordResponse> {
}
