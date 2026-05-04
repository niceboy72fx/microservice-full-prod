package com.risk.app.command.core;

public interface CommandHandler<C extends Command<R>, R> {

    Class<C> commandType();

    R handle(C command);
}
