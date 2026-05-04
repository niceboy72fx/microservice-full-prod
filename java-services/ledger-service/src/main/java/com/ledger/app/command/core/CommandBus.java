package com.ledger.app.command.core;

public interface CommandBus {

    <R, C extends Command<R>> R execute(C command);
}
