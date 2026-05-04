package com.commonservice.common.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SharedConstantsLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private SharedConstantsLoader() {
    }

    public static SharedConstants load() {
        Path path = resolvePath();
        try {
            String raw = Files.readString(path);
            return OBJECT_MAPPER.readValue(raw, SharedConstants.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load shared constants from: " + path, e);
        }
    }

    private static Path resolvePath() {
        String override = System.getProperty("SHARED_CONSTANTS_PATH");
        if (override == null || override.isBlank()) {
            override = System.getenv("SHARED_CONSTANTS_PATH");
        }

        if (override != null && !override.isBlank()) {
            Path p = Path.of(override).toAbsolutePath().normalize();
            if (Files.exists(p)) return p;
        }

        List<Path> candidates = List.of(
                Path.of("shared-config/constants-shared.json"),
                Path.of("../shared-config/constants-shared.json"),
                Path.of("../../shared-config/constants-shared.json")
        );

        for (Path candidate : candidates) {
            Path abs = candidate.toAbsolutePath().normalize();
            if (Files.exists(abs)) {
                return abs;
            }
        }

        throw new IllegalStateException("shared-config/constants-shared.json not found. Set SHARED_CONSTANTS_PATH");
    }
}
