package com.reporting.app.config;

import com.reporting.app.command.CommandBus;
import com.reporting.app.command.CommandRegistry;
import com.reporting.app.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
