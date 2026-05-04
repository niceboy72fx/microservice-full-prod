package com.commonservice.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class SharedTopicsLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SharedTopicsLoader() {
    }

    public static SharedTopics load() {
        Path path = SharedConfigPathResolver.resolve(
                "SHARED_TOPIC_PATH",
                List.of(
                        "shared-config/topic-shared.json",
                        "../shared-config/topic-shared.json",
                        "../../shared-config/topic-shared.json"
                )
        );

        try {
            String json = Files.readString(path);
            Map<String, Map<String, String>> data = OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
            return new SharedTopics(data);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load topics from " + path, e);
        }
    }
}
