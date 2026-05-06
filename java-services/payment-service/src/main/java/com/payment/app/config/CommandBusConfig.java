package com.payment.app.config;

import com.payment.app.command.core.CommandBus;
import com.payment.app.command.core.CommandRegistry;
import com.payment.app.command.core.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfig {

    @Bean
    public CommandBus commandBus(CommandRegistry commandRegistry) {
        return new SimpleCommandBus(commandRegistry);
    }
}
