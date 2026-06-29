package com.wajahat.ordersaga.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

    @Test
    void exposesErrorCode() {
        BusinessException exception = new BusinessException("ORDER_INVALID", "Order is invalid");

        assertEquals("ORDER_INVALID", exception.getErrorCode());
        assertEquals("Order is invalid", exception.getMessage());
    }
}
