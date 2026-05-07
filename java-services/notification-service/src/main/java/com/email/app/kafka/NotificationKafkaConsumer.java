package com.email.app.kafka;

import com.email.app.kafka.DlqPublisher;
import com.email.app.bean.NotificationEvent;
import com.email.app.bean.PasswordResetRequestedEvent;
import com.email.app.bean.SseFanoutMessage;
import com.email.app.service.SseFanoutPublisher;
import com.email.app.kafka.RetryPublisher;
import com.email.app.process.EmailSendWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationKafkaConsumer.class);

    private final ObjectMapper objectMapper;
    private final EmailSendWorker emailSendWorker;
    private final RetryPublisher retryPublisher;
    private final DlqPublisher dlqPublisher;
    private final StringRedisTemplate redisTemplate;
    private final SseFanoutPublisher sseFanoutPublisher;

    @Value("${app.notification.retry.max-attempts:6}")
    private int maxAttempts;

    public NotificationKafkaConsumer(
            ObjectMapper objectMapper,
            EmailSendWorker emailSendWorker,
            RetryPublisher retryPublisher,
            DlqPublisher dlqPublisher,
            StringRedisTemplate redisTemplate,
            SseFanoutPublisher sseFanoutPublisher
    ) {
        this.objectMapper = objectMapper;
        this.emailSendWorker = emailSendWorker;
        this.retryPublisher = retryPublisher;
        this.dlqPublisher = dlqPublisher;
        this.redisTemplate = redisTemplate;
        this.sseFanoutPublisher = sseFanoutPublisher;
    }

    @KafkaListener(
            topics = {
                    "${app.notification.topic.main:notification.email.v1}",
                    "${app.notification.topic.retry-1m:notification.email.retry.1m.v1}",
                    "${app.notification.topic.retry-5m:notification.email.retry.5m.v1}",
                    "${app.notification.topic.retry-30m:notification.email.retry.30m.v1}",
                    "${app.auth.topic.auth-events:auth-events}"
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        NotificationEvent event;
        try {
            event = mapRecord(record.value());
            if (alreadyProcessed(event.eventId())) {
                log.info("Skip duplicate notification eventId={} correlationId={}", event.eventId(), event.correlationId());
                acknowledgment.acknowledge();
                return;
            }

            emailSendWorker.sendAsync(event).orTimeout(15, TimeUnit.SECONDS).join();
            markProcessed(event.eventId());
            pushSse(event, "DELIVERED", "Notification sent");
            acknowledgment.acknowledge();
        } catch (Exception exception) {
            String reason = exception.getMessage() == null ? "UNKNOWN_ERROR" : exception.getMessage();
            log.warn("Notification delivery failed topic={} reason={}", record.topic(), reason);
            NotificationEvent fallbackEvent = eventFromSafe(record.value());

            if (shouldGoDlq(fallbackEvent, reason)) {
                dlqPublisher.publish(fallbackEvent, reason);
                pushSse(fallbackEvent, "DLQ", reason);
            } else {
                retryPublisher.publish(fallbackEvent, reason);
                pushSse(fallbackEvent, "RETRY", reason);
            }
            acknowledgment.acknowledge();
        }
    }

    private void pushSse(NotificationEvent event, String status, String message) {
        sseFanoutPublisher.publish(
                new SseFanoutMessage(
                        event.userId(),
                        "notification-status",
                        event.eventId(),
                        event.correlationId(),
                        status,
                        event.subject(),
                        message
                )
        );
    }

    private NotificationEvent mapRecord(String payload) throws JsonProcessingException {
        try {
            NotificationEvent direct = objectMapper.readValue(payload, NotificationEvent.class);
            if (direct.recipient() != null && !direct.recipient().isBlank()) {
                return direct;
            }
        } catch (Exception ignored) {
            // Fallback to auth event mapping.
        }

        PasswordResetRequestedEvent authEvent = objectMapper.readValue(payload, PasswordResetRequestedEvent.class);
        return new NotificationEvent(
                emptyToRandom(authEvent.eventId()),
                emptyToRandom(authEvent.correlationId()),
                emptyToRandom(authEvent.userId()),
                authEvent.email(),
                "Password reset request",
                "Your reset token is: " + authEvent.resetToken(),
                0
        );
    }

    private NotificationEvent eventFromSafe(String payload) {
        try {
            return mapRecord(payload);
        } catch (Exception ignored) {
            String id = UUID.randomUUID().toString();
            return new NotificationEvent(id, id, id, "unknown", "failed-parse", payload, maxAttempts);
        }
    }

    private boolean shouldGoDlq(NotificationEvent event, String reason) {
        if (event.retryCount() >= maxAttempts) {
            return true;
        }
        return reason.contains("Invalid") || reason.contains("deserialize") || reason.contains("parse");
    }

    private boolean alreadyProcessed(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(processedKey(eventId)));
    }

    private void markProcessed(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        redisTemplate.opsForValue().set(processedKey(eventId), "1", Duration.ofDays(7));
    }

    private String processedKey(String eventId) {
        return "notification:processed:" + eventId;
    }

    private String emptyToRandom(String value) {
        if (value == null || value.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return value;
    }
}
