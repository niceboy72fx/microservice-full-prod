package com.commonservice.common.dto;

public record CommonResponse<T>(
        boolean success,
        String message,
        T data
) {}
