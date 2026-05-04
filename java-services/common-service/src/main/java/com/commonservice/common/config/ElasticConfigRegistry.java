package com.commonservice.common.config;

public final class ElasticConfigRegistry {
    private static final ElasticSharedConfig CONFIG = ElasticSharedConfigLoader.load();

    private ElasticConfigRegistry() {
    }

    public static ElasticSharedConfig.ElasticsearchConfig elasticsearch() {
        return CONFIG.elasticsearch();
    }

    public static ElasticSharedConfig.KibanaConfig kibana() {
        return CONFIG.kibana();
    }

    public static ElasticSharedConfig.LoggingConfig logging() {
        return CONFIG.logging();
    }
}
