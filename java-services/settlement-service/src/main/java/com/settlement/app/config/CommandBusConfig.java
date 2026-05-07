package com.settlement.app.config;

import com.settlement.app.command.CommandBus;
import com.settlement.app.command.CommandRegistry;
import com.settlement.app.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
