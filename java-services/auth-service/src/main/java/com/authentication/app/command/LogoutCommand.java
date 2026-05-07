package com.authentication.app.command;

import com.authentication.app.command.Command;

public record LogoutCommand(String refreshToken) implements Command<Void> {
}
