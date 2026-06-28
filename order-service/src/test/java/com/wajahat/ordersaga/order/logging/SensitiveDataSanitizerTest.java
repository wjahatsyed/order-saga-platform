package com.wajahat.ordersaga.order.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SensitiveDataSanitizerTest {
    private final SensitiveDataSanitizer sanitizer = new SensitiveDataSanitizer();

    @Test
    void masksSensitiveMapFields() {
        Object sanitized = sanitizer.sanitize(Map.of(
                "username", "customer",
                "password", "customer123",
                "authorization", "Bearer token"
        ));

        assertEquals(Map.of(
                "username", "customer",
                "password", "****",
                "authorization", "****"
        ), sanitized);
    }

    @Test
    void masksSensitiveRecordFields() {
        Object sanitized = sanitizer.sanitize(new TokenResponse("jwt-token", "Bearer"));

        assertEquals(Map.of("token", "****", "tokenType", "Bearer"), sanitized);
        assertFalse(sanitized.toString().contains("jwt-token"));
    }

    private record TokenResponse(String token, String tokenType) {
    }
}
