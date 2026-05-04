package com.authentication.app.bootstrap;

import com.authentication.app.integration.external.ExternalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupComponent {

    private static final Logger log = LoggerFactory.getLogger(StartupComponent.class);

    private final ObjectProvider<KafkaTemplate<String, String>> kafkaTemplateProvider;
    private final ObjectProvider<StringRedisTemplate> redisTemplateProvider;
    private final ExternalClient externalClient;

    public StartupComponent(
            ObjectProvider<KafkaTemplate<String, String>> kafkaTemplateProvider,
            ObjectProvider<StringRedisTemplate> redisTemplateProvider,
            ExternalClient externalClient
    ) {
        this.kafkaTemplateProvider = kafkaTemplateProvider;
        this.redisTemplateProvider = redisTemplateProvider;
        this.externalClient = externalClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started. Kafka template available: {}", kafkaTemplateProvider.getIfAvailable() != null);
        log.info("Application started. Redis template available: {}", redisTemplateProvider.getIfAvailable() != null);
        log.info("Application started. gRPC client status: {}", externalClient.healthCheck());
    }
}
