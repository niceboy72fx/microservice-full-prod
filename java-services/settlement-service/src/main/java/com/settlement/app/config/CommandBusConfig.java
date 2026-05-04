package com.settlement.app.config;

import com.settlement.app.command.core.CommandBus;
import com.settlement.app.command.core.CommandRegistry;
import com.settlement.app.command.core.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
