package com.wajahat.ordersaga.notification.listener;

import com.wajahat.ordersaga.common.event.OrderCancelledEvent;
import com.wajahat.ordersaga.common.event.OrderConfirmedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OrderEventListenerTest {

    private final OrderEventListener listener = new OrderEventListener();

    @Test
    void handleOrderConfirmed_ShouldProcessEvent() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Instant.now()
        );

        assertDoesNotThrow(() -> listener.handleOrderConfirmed(event));
    }

    @Test
    void handleOrderCancelled_ShouldProcessEvent() {
        OrderCancelledEvent event = new OrderCancelledEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "payment failed", Instant.now()
        );

        assertDoesNotThrow(() -> listener.handleOrderCancelled(event));
    }
}
