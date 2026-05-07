package com.account.app.command;

import com.account.app.command.Command;

public record SoftDeleteAccountCommand(String accountId) implements Command<Void> {}
