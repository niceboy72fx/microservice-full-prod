package com.authentication.app.bootstrap;

import com.authentication.app.integration.external.ExternalClient;
import com.commonservice.common.observability.Logger;
import com.commonservice.common.observability.LoggerFactory;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupComponent {

    private static final Logger log = LoggerFactory.create("auth-service");

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
        log.info("Application started", Map.of(
                "kafkaTemplateAvailable", kafkaTemplateProvider.getIfAvailable() != null,
                "redisTemplateAvailable", redisTemplateProvider.getIfAvailable() != null,
                "grpcClientStatus", externalClient.healthCheck()
        ));
    }
}
