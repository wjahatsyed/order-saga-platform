package com.wajahat.ordersaga.common.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void testOrderCreatedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();
        List<OrderItemPayload> items = List.of(new OrderItemPayload(UUID.randomUUID(), 2, BigDecimal.TEN));
        
        OrderCreatedEvent event = new OrderCreatedEvent(eventId, orderId, customerId, items, BigDecimal.valueOf(20), now);
        
        assertEquals(eventId, event.eventId());
        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals(items, event.items());
        assertEquals(BigDecimal.valueOf(20), event.totalAmount());
        assertEquals(now, event.occurredAt());
    }

    @Test
    void testInventoryRejectedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();
        InventoryRejectedEvent event = new InventoryRejectedEvent(eventId, orderId, customerId, "Out of stock", now);
        assertEquals(eventId, event.eventId());
        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals("Out of stock", event.reason());
        assertEquals(now, event.occurredAt());
    }

    @Test
    void testInventoryReservedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();
        InventoryReservedEvent event = new InventoryReservedEvent(eventId, orderId, customerId, now);
        assertEquals(eventId, event.eventId());
        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals(now, event.occurredAt());
    }

    @Test
    void testPaymentCompletedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Instant now = Instant.now();
        PaymentCompletedEvent event = new PaymentCompletedEvent(eventId, orderId, customerId, amount, now);
        assertEquals(eventId, event.eventId());
        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals(amount, event.amount());
        assertEquals(now, event.occurredAt());
    }
    
    @Test
    void testPaymentFailedEvent() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Instant now = Instant.now();
        PaymentFailedEvent event = new PaymentFailedEvent(eventId, orderId, customerId, amount, "Insufficient funds", now);
        assertEquals(eventId, event.eventId());
        assertEquals(orderId, event.orderId());
        assertEquals(customerId, event.customerId());
        assertEquals(amount, event.amount());
        assertEquals("Insufficient funds", event.reason());
        assertEquals(now, event.occurredAt());
    }
}
