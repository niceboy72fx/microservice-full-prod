package com.authentication.app.config;

import com.authentication.app.command.core.CommandBus;
import com.authentication.app.command.core.CommandRegistry;
import com.authentication.app.command.core.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
