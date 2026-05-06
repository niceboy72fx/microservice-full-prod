package com.email.app.notification.dlq;

import com.email.app.notification.model.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class DlqPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.notification.topic.dlq:notification.email.dlq.v1}")
    private String dlqTopic;

    public DlqPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(NotificationEvent event, String reason) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(MessageBuilder.withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, dlqTopic)
                    .setHeader(KafkaHeaders.KEY, event.recipient())
                    .setHeader("eventId", event.eventId())
                    .setHeader("correlationId", event.correlationId())
                    .setHeader("retryCount", event.retryCount())
                    .setHeader("failureReason", reason)
                    .build());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize DLQ notification event", exception);
        }
    }
}
