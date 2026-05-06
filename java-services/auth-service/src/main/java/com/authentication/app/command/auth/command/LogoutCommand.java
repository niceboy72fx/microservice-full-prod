package com.authentication.app.command.auth.command;

import com.authentication.app.command.core.Command;

public record LogoutCommand(String refreshToken) implements Command<Void> {
}
