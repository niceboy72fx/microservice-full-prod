package com.authentication.app.command.auth.command.twofactor;

import com.authentication.app.command.core.Command;

public record TwoFactorDisableCommand(String username, String code) implements Command<Void> {
}
