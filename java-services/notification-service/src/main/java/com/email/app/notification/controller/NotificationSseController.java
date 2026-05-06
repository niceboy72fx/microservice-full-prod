package com.email.app.notification.controller;

import com.email.app.notification.security.JwtUserIdResolver;
import com.email.app.notification.sse.NotificationSseService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Component
@Scope("prototype")
@RequestMapping("/api/v1/notifications")
public class NotificationSseController {

    private final NotificationSseService notificationSseService;
    private final JwtUserIdResolver jwtUserIdResolver;

    public NotificationSseController(NotificationSseService notificationSseService, JwtUserIdResolver jwtUserIdResolver) {
        this.notificationSseService = notificationSseService;
        this.jwtUserIdResolver = jwtUserIdResolver;
    }

    @GetMapping(path = "/stream", produces = "text/event-stream")
    public SseEmitter stream(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = jwtUserIdResolver.resolveUserIdFromAuthorization(authorizationHeader);
        return notificationSseService.subscribe(userId);
    }
}
