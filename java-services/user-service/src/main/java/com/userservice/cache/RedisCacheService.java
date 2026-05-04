package com.userservice.cache;

import com.commonservice.common.cache.CacheService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisCacheService implements CacheService {
    private final Map<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String value) {
        store.put(key, value);
        System.out.println("[RedisCacheService] SET " + key + "=" + value);
    }

    @Override
    public Optional<String> get(String key) {
        String value = store.get(key);
        System.out.println("[RedisCacheService] GET " + key + " -> " + value);
        return Optional.ofNullable(value);
    }
}
