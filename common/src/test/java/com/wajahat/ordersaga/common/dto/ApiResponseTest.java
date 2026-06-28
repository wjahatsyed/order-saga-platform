package com.wajahat.ordersaga.common.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void createsApiResponse() {
        Instant timestamp = Instant.parse("2026-06-28T10:00:00Z");
        ApiResponse<String> response = new ApiResponse<>(true, "ok", "payload", timestamp);

        assertTrue(response.success());
        assertEquals("ok", response.message());
        assertEquals("payload", response.data());
        assertSame(timestamp, response.timestamp());
    }
}
