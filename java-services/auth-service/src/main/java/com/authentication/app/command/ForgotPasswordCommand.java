package com.authentication.app.command;

import com.authentication.app.command.Command;
import com.authentication.app.bean.ForgotPasswordResponse;

public record ForgotPasswordCommand(String email) implements Command<ForgotPasswordResponse> {
}
