package com.risk.app.command.core;

import com.risk.app.common.exception.BusinessException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CommandRegistry {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    public void register(CommandHandler<?, ?> handler) {
        handlers.put(handler.commandType(), handler);
    }

    @SuppressWarnings("unchecked")
    public <R, C extends Command<R>> CommandHandler<C, R> getHandler(Class<C> commandType) {
        CommandHandler<?, ?> handler = handlers.get(commandType);
        if (handler == null) {
            throw new BusinessException("COMMAND_HANDLER_NOT_FOUND", "No handler registered for " + commandType.getSimpleName());
        }
        return (CommandHandler<C, R>) handler;
    }
}
