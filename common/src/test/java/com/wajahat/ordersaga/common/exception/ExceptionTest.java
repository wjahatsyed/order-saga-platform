package com.wajahat.ordersaga.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExceptionTest {

    @Test
    void testBusinessException() {
        BusinessException ex = new BusinessException("CODE", "Message");
        assertEquals("CODE", ex.getErrorCode());
        assertEquals("Message", ex.getMessage());
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode());
        assertEquals("Not found", ex.getMessage());
    }
}
