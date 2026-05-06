package com.account.app.command.account.command;

import com.account.app.command.core.Command;

public record SoftDeleteAccountCommand(String accountId) implements Command<Void> {}
