package com.authentication.app.config;

import com.authentication.app.command.CommandBus;
import com.authentication.app.command.CommandRegistry;
import com.authentication.app.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
