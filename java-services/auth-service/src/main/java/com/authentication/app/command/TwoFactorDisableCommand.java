package com.authentication.app.command;

import com.authentication.app.command.Command;

public record TwoFactorDisableCommand(String username, String code) implements Command<Void> {
}
