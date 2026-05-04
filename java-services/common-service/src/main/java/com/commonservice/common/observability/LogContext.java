package com.commonservice.common.observability;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class LogContext {
    private final Map<String, Object> fields;

    public LogContext(Map<String, Object> fields) {
        this.fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
    }

    public Map<String, Object> fields() {
        return fields;
    }

    public static LogContext of(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return new LogContext(map);
    }
}
