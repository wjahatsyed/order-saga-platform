package com.wajahat.ordersaga.common.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class DtoTest {

    @Test
    void testApiResponse() {
        ApiResponse<String> response = new ApiResponse<>(true, "msg", "data", Instant.now());
        assertTrue(response.success());
        assertEquals("data", response.data());
        assertEquals("msg", response.message());
    }

    @Test
    void testErrorResponse() {
        Instant now = Instant.now();
        ErrorResponse response = new ErrorResponse("CODE", "message", "/path", now);
        assertEquals("CODE", response.code());
        assertEquals("message", response.message());
        assertEquals("/path", response.path());
        assertEquals(now, response.timestamp());
    }

    @Test
    void testPageResponse() {
        java.util.List<String> content = java.util.List.of("a", "b");
        PageResponse<String> response = new PageResponse<>(content, 1, 10, 100, 10);
        assertEquals(content, response.content());
        assertEquals(1, response.page());
        assertEquals(10, response.size());
        assertEquals(100, response.totalElements());
        assertEquals(10, response.totalPages());
    }
}
