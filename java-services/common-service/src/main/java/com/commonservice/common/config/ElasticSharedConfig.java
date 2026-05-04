package com.commonservice.common.config;

public record ElasticSharedConfig(
        ElasticsearchConfig elasticsearch,
        KibanaConfig kibana,
        LoggingConfig logging
) {
    public record ElasticsearchConfig(
            String node,
            String username,
            String password,
            int requestTimeoutMs,
            int maxRetries,
            boolean sniffOnStart
    ) {
    }

    public record KibanaConfig(String url) {
    }

    public record LoggingConfig(String indexPrefix, String level) {
    }
}
