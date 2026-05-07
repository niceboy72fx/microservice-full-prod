package com.payment.app.command;

public interface CommandBus {

    <R, C extends Command<R>> R execute(C command);
}
