package com.commonservice.common.idempotency;

import java.util.Objects;
import java.util.UUID;

public record IdempotencyKey(String value) {
    public IdempotencyKey {
        Objects.requireNonNull(value, "idempotency key must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("idempotency key must not be blank");
        }
    }

    public static IdempotencyKey random() {
        return new IdempotencyKey(UUID.randomUUID().toString());
    }
}
