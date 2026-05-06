package com.email.app.config;

import com.email.app.command.core.CommandBus;
import com.email.app.command.core.CommandRegistry;
import com.email.app.command.core.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
