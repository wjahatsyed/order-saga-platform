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

    @Test
    void sanitizeRecordWithException() {
        record FaultyRecord(String name) {
            @Override
            public String name() {
                throw new RuntimeException("Reflective access failure");
            }
        }
        Object sanitized = sanitizer.sanitize(new FaultyRecord("test"));
        assertEquals(Map.of("name", "<unavailable>"), sanitized);
    }

    @Test
    void sanitizeNestedStructures() {
        Map<String, Object> nested = Map.of(
                "outer", Map.of("password", "secret", "data", "info"),
                "list", java.util.List.of(Map.of("token", "abc"))
        );
        Object sanitized = sanitizer.sanitize(nested);
        Map<String, Object> expected = Map.of(
                "outer", Map.of("password", "****", "data", "info"),
                "list", java.util.List.of(Map.of("token", "****"))
        );
        assertEquals(expected, sanitized);
    }

    @Test
    void sanitizeEnum() {
        assertEquals(com.wajahat.ordersaga.common.enums.Role.CUSTOMER, sanitizer.sanitize(com.wajahat.ordersaga.common.enums.Role.CUSTOMER));
    }

    @Test
    void sanitizeLong() {
        assertEquals(123L, sanitizer.sanitize(123L));
    }

    @Test
    void sanitizeBoolean() {
        assertEquals(false, sanitizer.sanitize(false));
    }

    private record TokenResponse(String token, String tokenType) {
    }
}
