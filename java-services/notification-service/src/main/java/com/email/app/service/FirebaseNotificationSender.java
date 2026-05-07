package com.email.app.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FirebaseNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationSender.class);

    private final FirebaseProperties properties;
    private final FirebaseApp firebaseApp;

    public FirebaseNotificationSender(FirebaseProperties properties, @Nullable FirebaseApp firebaseApp) {
        this.properties = properties;
        this.firebaseApp = firebaseApp;
    }

    public void send(String deviceToken, String title, String body) {
        if (!properties.enabled()) {
            log.debug("Firebase disabled, skip send");
            return;
        }
        if (firebaseApp == null) {
            throw new IllegalStateException("FirebaseApp is not initialized");
        }

        try {
            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();
            FirebaseMessaging.getInstance(firebaseApp).send(message);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to send Firebase notification", exception);
        }
    }
}
