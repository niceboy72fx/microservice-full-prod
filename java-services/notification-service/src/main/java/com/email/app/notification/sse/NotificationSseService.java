package com.email.app.notification.sse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationSseService {

    private final Map<String, Set<SseEmitter>> subscribers = new ConcurrentHashMap<>();
    private final long sseTimeoutMs;

    public NotificationSseService(@Value("${app.notification.sse.timeout-ms:0}") long sseTimeoutMs) {
        this.sseTimeoutMs = sseTimeoutMs;
    }

    public SseEmitter subscribe(String userId) {
        String key = normalizeUserId(userId);
        SseEmitter emitter = new SseEmitter(sseTimeoutMs);

        subscribers.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(key, emitter));
        emitter.onTimeout(() -> removeEmitter(key, emitter));
        emitter.onError(ignored -> removeEmitter(key, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("subscribed:" + key));
        } catch (IOException ignored) {
            removeEmitter(key, emitter);
        }
        return emitter;
    }

    public void publishToUserId(String userId, String eventName, SseNotificationPayload payload) {
        String key = normalizeUserId(userId);
        Set<SseEmitter> emitters = subscribers.get(key);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .id(payload.eventId())
                        .data(payload));
            } catch (IOException exception) {
                removeEmitter(key, emitter);
            }
        }
    }

    private void removeEmitter(String key, SseEmitter emitter) {
        Set<SseEmitter> emitters = subscribers.get(key);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            subscribers.remove(key);
        }
    }

    private String normalizeUserId(String userId) {
        return userId == null ? "" : userId.trim();
    }
}
