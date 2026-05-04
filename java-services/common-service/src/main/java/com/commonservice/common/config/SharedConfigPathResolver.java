package com.commonservice.common.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SharedConfigPathResolver {
    private SharedConfigPathResolver() {
    }

    public static Path resolve(String envVarOrProperty, List<String> fallbackRelativePaths) {
        String override = System.getProperty(envVarOrProperty);
        if (override == null || override.isBlank()) {
            override = System.getenv(envVarOrProperty);
        }
        if (override != null && !override.isBlank()) {
            Path p = Path.of(override).toAbsolutePath().normalize();
            if (Files.exists(p)) return p;
        }

        for (String relative : fallbackRelativePaths) {
            Path p = Path.of(relative).toAbsolutePath().normalize();
            if (Files.exists(p)) return p;
        }

        throw new IllegalStateException("Shared config not found for " + envVarOrProperty);
    }
}
