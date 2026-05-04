package com.commonservice.common.config;

import java.util.Map;

public record SharedEndpoints(Map<String, EndpointConfig> endpoints) {
    public record EndpointConfig(String baseUrl, String graphql) {
    }
}
