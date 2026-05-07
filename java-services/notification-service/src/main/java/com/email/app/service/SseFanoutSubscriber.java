package com.email.app.service;

import com.email.app.service.NotificationSseService;
import com.email.app.bean.SseNotificationPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class SseFanoutSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final NotificationSseService notificationSseService;

    public SseFanoutSubscriber(ObjectMapper objectMapper, NotificationSseService notificationSseService) {
        this.objectMapper = objectMapper;
        this.notificationSseService = notificationSseService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            SseFanoutMessage fanoutMessage = objectMapper.readValue(message.getBody(), SseFanoutMessage.class);
            notificationSseService.publishToUserId(
                    fanoutMessage.userId(),
                    fanoutMessage.eventName(),
                    new SseNotificationPayload(
                            fanoutMessage.eventId(),
                            fanoutMessage.correlationId(),
                            fanoutMessage.userId(),
                            fanoutMessage.status(),
                            fanoutMessage.subject(),
                            fanoutMessage.message()
                    )
            );
        } catch (Exception ignored) {
            // Ignore malformed message and keep listener alive.
        }
    }
}
