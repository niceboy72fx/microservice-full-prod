package com.userservice.service;

import com.commonservice.common.cache.CacheService;
import com.commonservice.common.event.EventEnvelope;
import com.commonservice.common.event.EventPublisher;
import com.commonservice.common.event.UserCreatedEvent;
import com.commonservice.common.json.JsonUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class UserService {
    private final EventPublisher eventPublisher;
    private final CacheService cacheService;

    public UserService(EventPublisher eventPublisher, CacheService cacheService) {
        this.eventPublisher = eventPublisher;
        this.cacheService = cacheService;
    }

    public String createUser(String email, String displayName) {
        String userId = UUID.randomUUID().toString();

        Map<String, String> userData = new LinkedHashMap<>();
        userData.put("id", userId);
        userData.put("email", email);
        userData.put("displayName", displayName);

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId, email, displayName);
        EventEnvelope<UserCreatedEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                "USER_CREATED",
                Instant.now(),
                userCreatedEvent
        );

        eventPublisher.publish(envelope);
        cacheService.put("user:" + userId, JsonUtils.toJson(userData));

        return userId;
    }
}
