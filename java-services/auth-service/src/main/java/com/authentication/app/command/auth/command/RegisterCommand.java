package com.authentication.app.command.auth.command;

import com.authentication.app.command.core.Command;
import com.authentication.app.dto.response.auth.LoginResponse;

public record RegisterCommand(String username, String password) implements Command<LoginResponse> {
}
