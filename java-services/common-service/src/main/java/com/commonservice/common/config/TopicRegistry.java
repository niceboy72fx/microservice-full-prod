package com.commonservice.common.config;

import java.util.Map;

public final class TopicRegistry {
    private static final SharedTopics SHARED_TOPICS = SharedTopicsLoader.load();

    private TopicRegistry() {
    }

    public static String get(String domain, String key) {
        Map<String, String> domainTopics = SHARED_TOPICS.topics().get(domain);
        if (domainTopics == null) throw new IllegalArgumentException("Unknown topic domain: " + domain);
        String topic = domainTopics.get(key);
        if (topic == null) throw new IllegalArgumentException("Unknown topic key: " + domain + "." + key);
        return topic;
    }
}
