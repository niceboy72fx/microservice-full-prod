package com.email.app.command.core;

public interface CommandBus {

    <R, C extends Command<R>> R execute(C command);
}
