package com.commonservice.common.config;

import com.commonservice.common.circuitbreaker.CircuitBreakerConfigProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.convert.DurationStyle;

public final class CircuitBreakerSharedConfigLoader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CircuitBreakerSharedConfigLoader() {
    }

    public static Optional<CircuitBreakerConfigProperties> loadOptional() {
        Path path = resolveOptionalPath();
        if (path == null) {
            return Optional.empty();
        }

        try {
            String json = Files.readString(path);
            Map<String, Object> root = OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
            CircuitBreakerConfigProperties result = new CircuitBreakerConfigProperties();

            Object defaults = root.get("defaults");
            if (defaults instanceof Map<?, ?> defaultsMap) {
                applyRule(result.getDefaults(), defaultsMap);
            }

            Object instances = root.get("instances");
            if (instances instanceof Map<?, ?> instanceMapRaw) {
                Map<String, CircuitBreakerConfigProperties.Rule> instancesMap = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : instanceMapRaw.entrySet()) {
                    if (!(entry.getKey() instanceof String key) || !(entry.getValue() instanceof Map<?, ?> value)) {
                        continue;
                    }
                    CircuitBreakerConfigProperties.Rule rule = new CircuitBreakerConfigProperties.Rule();
                    applyRule(rule, value);
                    instancesMap.put(key, rule);
                }
                result.setInstances(instancesMap);
            }
            return Optional.of(result);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load circuit breaker shared config from " + path, exception);
        }
    }

    private static void applyRule(CircuitBreakerConfigProperties.Rule rule, Map<?, ?> map) {
        Integer failureThreshold = asInt(map.get("failureThreshold"));
        Integer minimumCalls = asInt(map.get("minimumCalls"));
        Integer halfOpenMaxCalls = asInt(map.get("halfOpenMaxCalls"));
        Duration openDuration = asDuration(map.get("openDuration"));

        if (failureThreshold != null) {
            rule.setFailureThreshold(failureThreshold);
        }
        if (minimumCalls != null) {
            rule.setMinimumCalls(minimumCalls);
        }
        if (halfOpenMaxCalls != null) {
            rule.setHalfOpenMaxCalls(halfOpenMaxCalls);
        }
        if (openDuration != null) {
            rule.setOpenDuration(openDuration);
        }
    }

    private static Integer asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Integer.parseInt(text.trim());
        }
        return null;
    }

    private static Duration asDuration(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return Duration.ofMillis(number.longValue());
        }
        if (value instanceof String text && !text.isBlank()) {
            return DurationStyle.detectAndParse(text.trim());
        }
        return null;
    }

    private static Path resolveOptionalPath() {
        String override = System.getProperty("SHARED_CIRCUIT_BREAKER_PATH");
        if (override == null || override.isBlank()) {
            override = System.getenv("SHARED_CIRCUIT_BREAKER_PATH");
        }

        List<String> candidates = new ArrayList<>();
        if (override != null && !override.isBlank()) {
            candidates.add(override);
        }

        candidates.add("shared-config/circuit-breaker-shared.json");
        candidates.add("../shared-config/circuit-breaker-shared.json");
        candidates.add("../../shared-config/circuit-breaker-shared.json");

        for (String candidate : candidates) {
            Path path = Path.of(candidate).toAbsolutePath().normalize();
            if (Files.exists(path)) {
                return path;
            }
        }
        return null;
    }
}
