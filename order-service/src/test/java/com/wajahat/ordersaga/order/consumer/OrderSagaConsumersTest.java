package com.wajahat.ordersaga.order.consumer;

import static org.mockito.Mockito.verify;

import com.wajahat.ordersaga.common.event.InventoryRejectedEvent;
import com.wajahat.ordersaga.common.event.PaymentCompletedEvent;
import com.wajahat.ordersaga.common.event.PaymentFailedEvent;
import com.wajahat.ordersaga.order.service.OrderService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderSagaConsumersTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderSagaConsumers consumers;

    @Test
    void testHandleInventoryRejected() {
        UUID orderId = UUID.randomUUID();
        InventoryRejectedEvent event = new InventoryRejectedEvent(UUID.randomUUID(), orderId, UUID.randomUUID(), "Rejected", Instant.now());
        consumers.handleInventoryRejected(event);
        verify(orderService).cancelOrder(orderId, "Rejected");
    }

    @Test
    void testHandlePaymentCompleted() {
        UUID orderId = UUID.randomUUID();
        PaymentCompletedEvent event = new PaymentCompletedEvent(UUID.randomUUID(), orderId, UUID.randomUUID(), BigDecimal.TEN, Instant.now());
        consumers.handlePaymentCompleted(event);
        verify(orderService).confirmOrder(orderId);
    }

    @Test
    void testHandlePaymentFailed() {
        UUID orderId = UUID.randomUUID();
        PaymentFailedEvent event = new PaymentFailedEvent(UUID.randomUUID(), orderId, UUID.randomUUID(), BigDecimal.TEN, "Failed", Instant.now());
        consumers.handlePaymentFailed(event);
        verify(orderService).cancelOrder(orderId, "Failed");
    }
}
