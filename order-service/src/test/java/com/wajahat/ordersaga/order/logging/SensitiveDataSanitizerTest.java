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

    @Test
    void sanitizeSimpleValues() {
        assertEquals("test", sanitizer.sanitize("test"));
        assertEquals(123, sanitizer.sanitize(123));
        assertEquals(true, sanitizer.sanitize(true));
        assertEquals(null, sanitizer.sanitize(null));
    }

    @Test
    void sanitizeThrowable() {
        Exception ex = new RuntimeException("error");
        assertEquals("RuntimeException: error", sanitizer.sanitize(ex));
    }

    @Test
    void sanitizeCollection() {
        java.util.List<String> list = java.util.List.of("a", "b");
        assertEquals(list, sanitizer.sanitize(list));
    }

    @Test
    void sanitizeArray() {
        String[] array = new String[]{"a", "b"};
        assertEquals(java.util.List.of("a", "b"), sanitizer.sanitize(array));
    }

    private record TokenResponse(String token, String tokenType) {
    }
}
