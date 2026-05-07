package com.commonservice.common.dto;

import com.commonservice.common.enumtype.NotificationType;
import java.time.Instant;
import java.util.Map;

public record NotificationEventDto(
        String notificationId,
        String userId,
        NotificationType type,
        String title,
        String content,
        Map<String, String> metadata,
        Instant createdAt
) {}
