package com.example.common.util;

public final class StringUtils {
    
    private StringUtils() {
        // Prevent instantiation
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
