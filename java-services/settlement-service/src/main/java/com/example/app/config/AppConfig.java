package com.example.app.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel grpcManagedChannel(
            @Value("${grpc.client.host}") String host,
            @Value("${grpc.client.port}") int port
    ) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }
}
