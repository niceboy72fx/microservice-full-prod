package com.authentication.app.command;

import com.authentication.app.command.Command;
import com.authentication.app.bean.LoginResponse;

public record RegisterCommand(String username, String password) implements Command<LoginResponse> {
}
