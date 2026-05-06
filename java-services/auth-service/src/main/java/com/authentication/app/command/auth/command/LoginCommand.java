package com.authentication.app.command.auth.command;

import com.authentication.app.command.core.Command;
import com.authentication.app.dto.response.auth.LoginResponse;

public record LoginCommand(String username, String password, String otpCode, String ipAddress) implements Command<LoginResponse> {
}
