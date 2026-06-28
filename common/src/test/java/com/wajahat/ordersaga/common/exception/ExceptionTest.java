package com.wajahat.ordersaga.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExceptionTest {

    @Test
    void testBusinessException() {
        BusinessException ex = new BusinessException("CODE", "Message");
        assertEquals("CODE", ex.getErrorCode());
        assertEquals("Message", ex.getMessage());

        Throwable cause = new RuntimeException("cause");
        BusinessException exWithCause = new BusinessException("CODE2", "Message2", cause);
        assertEquals("CODE2", exWithCause.getErrorCode());
        assertEquals("Message2", exWithCause.getMessage());
        assertEquals(cause, exWithCause.getCause());
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode());
        assertEquals("Not found", ex.getMessage());
    }

    @Test
    void testUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");
        assertEquals("UNAUTHORIZED", ex.getErrorCode());
        assertEquals("Unauthorized", ex.getMessage());
    }
}
