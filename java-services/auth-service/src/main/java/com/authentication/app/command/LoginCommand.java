package com.authentication.app.command;

import com.authentication.app.command.Command;
import com.authentication.app.bean.LoginResponse;

public record LoginCommand(String username, String password, String otpCode, String ipAddress) implements Command<LoginResponse> {
}
