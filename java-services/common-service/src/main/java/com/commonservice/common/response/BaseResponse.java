package com.commonservice.common.response;

public record BaseResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {}
