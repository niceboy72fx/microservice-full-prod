package com.example.app.config;

import com.example.app.command.core.CommandBus;
import com.example.app.command.core.CommandRegistry;
import com.example.app.command.core.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
