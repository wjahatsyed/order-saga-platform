package com.wajahat.ordersaga.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wajahat.ordersaga.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderDtoTest {

    @Test
    void testCreateOrderItemRequest() {
        UUID productId = UUID.randomUUID();
        CreateOrderItemRequest request = new CreateOrderItemRequest(productId, 2, BigDecimal.TEN);
        assertEquals(productId, request.productId());
        assertEquals(2, request.quantity());
        assertEquals(BigDecimal.TEN, request.unitPrice());
    }

    @Test
    void testCreateOrderRequest() {
        UUID customerId = UUID.randomUUID();
        List<CreateOrderItemRequest> items = List.of(new CreateOrderItemRequest(UUID.randomUUID(), 1, BigDecimal.ONE));
        CreateOrderRequest request = new CreateOrderRequest(customerId, items);
        assertEquals(customerId, request.customerId());
        assertEquals(items, request.items());
    }

    @Test
    void testOrderResponse() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();
        OrderResponse response = new OrderResponse(orderId, customerId, BigDecimal.TEN, OrderStatus.PENDING, now);
        assertEquals(orderId, response.orderId());
        assertEquals(customerId, response.customerId());
        assertEquals(BigDecimal.TEN, response.totalAmount());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(now, response.createdAt());
    }
}
