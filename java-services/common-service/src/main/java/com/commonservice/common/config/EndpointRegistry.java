package com.commonservice.common.config;

public final class EndpointRegistry {
    private static final SharedEndpoints SHARED_ENDPOINTS = SharedEndpointsLoader.load();

    private EndpointRegistry() {
    }

    public static SharedEndpoints.EndpointConfig get(String serviceKey) {
        SharedEndpoints.EndpointConfig endpoint = SHARED_ENDPOINTS.endpoints().get(serviceKey);
        if (endpoint == null) {
            throw new IllegalArgumentException("Unknown endpoint key: " + serviceKey);
        }
        return endpoint;
    }
}
