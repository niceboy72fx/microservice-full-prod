package com.email.app.notification.config;

public final class NotificationTopics {

    public static final String MAIN = "notification.email.v1";
    public static final String RETRY_1M = "notification.email.retry.1m.v1";
    public static final String RETRY_5M = "notification.email.retry.5m.v1";
    public static final String RETRY_30M = "notification.email.retry.30m.v1";
    public static final String DLQ = "notification.email.dlq.v1";

    private NotificationTopics() {
    }
}
