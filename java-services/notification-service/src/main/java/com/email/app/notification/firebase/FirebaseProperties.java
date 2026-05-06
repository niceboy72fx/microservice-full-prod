package com.email.app.notification.firebase;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notification.firebase")
public record FirebaseProperties(
        boolean enabled,
        String projectId,
        String credentialsPath
) {
}
