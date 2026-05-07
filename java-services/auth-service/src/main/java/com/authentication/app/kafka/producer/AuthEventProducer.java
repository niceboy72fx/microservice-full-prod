package com.authentication.app.kafka;

import com.authentication.app.kafka.event.PasswordResetRequestedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class AuthEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AuthEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishPasswordResetRequested(String topic, PasswordResetRequestedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            Message<String> message = MessageBuilder.withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, event.email())
                    .setHeader("correlationId", event.correlationId())
                    .setHeader("eventId", event.eventId())
                    .setHeader("eventType", event.eventType())
                    .build();
            kafkaTemplate.send(message);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize password reset event", exception);
        }
    }
}
