package com.ledger.app.command.core;

public class SimpleCommandBus implements CommandBus {

    private final CommandRegistry commandRegistry;

    public SimpleCommandBus(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public <R, C extends Command<R>> R execute(C command) {
        CommandHandler<C, R> handler = commandRegistry.getHandler((Class<C>) command.getClass());
        return handler.handle(command);
    }
}
