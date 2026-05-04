package com.commonservice.common.exception;

import java.util.Map;

public record SharedConstants(
        Map<String, ErrorCodeEntry> httpClientCode,
        Map<String, ErrorCodeEntry> serviceCallCode,
        Map<String, Object> commonConstant,
        Map<String, Object> roleConstant
) {
}
