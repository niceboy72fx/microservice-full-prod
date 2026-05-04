package com.commonservice.common.config;

import java.util.Map;

public record SharedTopics(Map<String, Map<String, String>> topics) {
}
