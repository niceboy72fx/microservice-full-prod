package com.ledger.app.config;

import com.ledger.app.command.core.CommandBus;
import com.ledger.app.command.core.CommandRegistry;
import com.ledger.app.command.core.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
