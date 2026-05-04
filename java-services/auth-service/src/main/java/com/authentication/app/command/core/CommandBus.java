package com.authentication.app.command.core;

public interface CommandBus {

    <R, C extends Command<R>> R execute(C command);
}
