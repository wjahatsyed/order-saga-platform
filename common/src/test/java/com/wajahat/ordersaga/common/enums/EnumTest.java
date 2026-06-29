package com.wajahat.ordersaga.common.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

class EnumTest {

    @Test
    void testEventType() {
        assertEquals("ORDER_CREATED", EventType.ORDER_CREATED.name());
        assertEquals("ORDER_CANCELLED", EventType.ORDER_CANCELLED.name());
        assertEquals("ORDER_CONFIRMED", EventType.ORDER_CONFIRMED.name());
        assertEquals("INVENTORY_RESERVED", EventType.INVENTORY_RESERVED.name());
        assertEquals("INVENTORY_REJECTED", EventType.INVENTORY_REJECTED.name());
        assertEquals("INVENTORY_RELEASED", EventType.INVENTORY_RELEASED.name());
        assertEquals("PAYMENT_COMPLETED", EventType.PAYMENT_COMPLETED.name());
        assertEquals("PAYMENT_FAILED", EventType.PAYMENT_FAILED.name());
        assertEquals("PAYMENT_REFUNDED", EventType.PAYMENT_REFUNDED.name());
    }

    @Test
    void testOrderStatus() {
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("CONFIRMED", OrderStatus.CONFIRMED.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }

    @Test
    void testInventoryStatus() {
        assertEquals("AVAILABLE", InventoryStatus.AVAILABLE.name());
        assertEquals("RESERVED", InventoryStatus.RESERVED.name());
        assertEquals("REJECTED", InventoryStatus.REJECTED.name());
        assertEquals("RELEASED", InventoryStatus.RELEASED.name());
    }

    @Test
    void testPaymentStatus() {
        assertEquals("PENDING", PaymentStatus.PENDING.name());
        assertEquals("COMPLETED", PaymentStatus.COMPLETED.name());
        assertEquals("FAILED", PaymentStatus.FAILED.name());
        assertEquals("REFUNDED", PaymentStatus.REFUNDED.name());
    }

    @Test
    void testRole() {
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("CUSTOMER", Role.CUSTOMER.name());
        assertEquals("SERVICE", Role.SERVICE.name());
    }
}
