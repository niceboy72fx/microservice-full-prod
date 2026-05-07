package com.email.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SseFanoutPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.notification.sse.redis-channel:notification:sse:fanout}")
    private String redisChannel;

    public SseFanoutPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(SseFanoutMessage message) {
        try {
            redisTemplate.convertAndSend(redisChannel, objectMapper.writeValueAsString(message));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to publish SSE fanout message", exception);
        }
    }
}
