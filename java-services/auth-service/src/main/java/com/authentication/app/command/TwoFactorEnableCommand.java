package com.authentication.app.command;

import com.authentication.app.command.Command;

public record TwoFactorEnableCommand(String username, String code) implements Command<Void> {
}
