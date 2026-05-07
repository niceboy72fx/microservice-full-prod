package com.ledger.app.config;

import com.ledger.app.command.CommandBus;
import com.ledger.app.command.CommandRegistry;
import com.ledger.app.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
