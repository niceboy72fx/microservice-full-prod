package com.commonservice.common.exception;

import java.util.Map;

public final class ErrorCodeRegistry {
    private static final SharedConstants CONSTANTS = SharedConstantsLoader.load();

    private ErrorCodeRegistry() {
    }

    public static ErrorCodeEntry getHttpClientCode(String key) {
        return getRequired(CONSTANTS.httpClientCode(), key, "httpClientCode");
    }

    public static ErrorCodeEntry getServiceCallCode(String key) {
        return getRequired(CONSTANTS.serviceCallCode(), key, "serviceCallCode");
    }

    private static ErrorCodeEntry getRequired(Map<String, ErrorCodeEntry> section, String key, String sectionName) {
        ErrorCodeEntry value = section.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing key '" + key + "' in section '" + sectionName + "'");
        }
        return value;
    }
}
