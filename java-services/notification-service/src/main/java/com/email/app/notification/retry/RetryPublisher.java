package com.email.app.notification.retry;

import com.email.app.notification.model.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class RetryPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.notification.topic.retry-1m:notification.email.retry.1m.v1}")
    private String retry1mTopic;

    @Value("${app.notification.topic.retry-5m:notification.email.retry.5m.v1}")
    private String retry5mTopic;

    @Value("${app.notification.topic.retry-30m:notification.email.retry.30m.v1}")
    private String retry30mTopic;

    public RetryPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(NotificationEvent event, String reason) {
        NotificationEvent next = event.withRetryCount(event.retryCount() + 1);
        String topic = resolveRetryTopic(next.retryCount());

        try {
            String payload = objectMapper.writeValueAsString(next);
            kafkaTemplate.send(MessageBuilder.withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, next.recipient())
                    .setHeader("eventId", next.eventId())
                    .setHeader("correlationId", next.correlationId())
                    .setHeader("retryCount", next.retryCount())
                    .setHeader("failureReason", reason)
                    .build());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize retry notification event", exception);
        }
    }

    private String resolveRetryTopic(int retryCount) {
        if (retryCount <= 2) {
            return retry1mTopic;
        }
        if (retryCount <= 4) {
            return retry5mTopic;
        }
        return retry30mTopic;
    }
}
