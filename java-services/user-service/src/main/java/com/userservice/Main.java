package com.userservice;

import com.userservice.cache.RedisCacheService;
import com.userservice.messaging.KafkaEventPublisher;
import com.userservice.service.UserService;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        KafkaEventPublisher eventPublisher = new KafkaEventPublisher();
        RedisCacheService cacheService = new RedisCacheService();
        UserService userService = new UserService(eventPublisher, cacheService);

        System.out.println("[Main] Creating user...");
        String userId = userService.createUser("alice@example.com", "Alice");

        System.out.println("[Main] Created userId=" + userId);
        cacheService.get("user:" + userId)
                .ifPresent(value -> System.out.println("[Main] Cached user payload=" + value));
    }
}
