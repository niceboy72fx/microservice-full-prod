package com.commonservice.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ElasticSharedConfigLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ElasticSharedConfigLoader() {
    }

    public static ElasticSharedConfig load() {
        Path path = SharedConfigPathResolver.resolve(
                "SHARED_ELASTIC_PATH",
                List.of(
                        "shared-config/elastic-shared.json",
                        "../shared-config/elastic-shared.json",
                        "../../shared-config/elastic-shared.json"
                )
        );

        try {
            return OBJECT_MAPPER.readValue(Files.readString(path), ElasticSharedConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load elastic config from " + path, e);
        }
    }
}
