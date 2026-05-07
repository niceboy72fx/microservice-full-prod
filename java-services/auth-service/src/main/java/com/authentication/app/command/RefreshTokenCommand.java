package com.authentication.app.command;

import com.authentication.app.command.Command;
import com.authentication.app.bean.LoginResponse;

public record RefreshTokenCommand(String refreshToken) implements Command<LoginResponse> {
}
