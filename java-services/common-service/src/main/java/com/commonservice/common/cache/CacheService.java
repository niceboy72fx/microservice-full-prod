package com.commonservice.common.cache;

import java.util.Optional;
import java.time.Duration;

public interface CacheService {
    void put(String key, String value);
    void put(String key, String value, Duration ttl);
    Optional<String> get(String key);
    boolean exists(String key);
}
