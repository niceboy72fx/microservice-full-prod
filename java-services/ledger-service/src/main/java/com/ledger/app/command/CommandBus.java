package com.ledger.app.command;

public interface CommandBus {

    <R, C extends Command<R>> R execute(C command);
}
