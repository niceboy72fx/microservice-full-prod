package com.commonservice.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SharedEndpointsLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SharedEndpointsLoader() {
    }

    public static SharedEndpoints load() {
        Path path = SharedConfigPathResolver.resolve(
                "SHARED_ENDPOINT_PATH",
                List.of(
                        "shared-config/endpoint-shared.json",
                        "shared-config/endpoint=shared.json",
                        "../shared-config/endpoint-shared.json",
                        "../shared-config/endpoint=shared.json",
                        "../../shared-config/endpoint-shared.json",
                        "../../shared-config/endpoint=shared.json"
                )
        );

        try {
            String json = Files.readString(path);
            Map<String, Map<String, String>> raw = OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
            Map<String, SharedEndpoints.EndpointConfig> map = new LinkedHashMap<>();
            for (Map.Entry<String, Map<String, String>> e : raw.entrySet()) {
                Map<String, String> v = e.getValue();
                map.put(e.getKey(), new SharedEndpoints.EndpointConfig(v.get("baseUrl"), v.get("graphql")));
            }
            return new SharedEndpoints(map);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load endpoints from " + path, e);
        }
    }
}
