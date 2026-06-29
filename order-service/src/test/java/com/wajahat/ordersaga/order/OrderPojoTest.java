package com.wajahat.ordersaga.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wajahat.ordersaga.common.enums.OrderStatus;
import com.wajahat.ordersaga.order.dto.CreateOrderItemRequest;
import com.wajahat.ordersaga.order.dto.CreateOrderRequest;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import com.wajahat.ordersaga.order.entity.OrderItemEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderPojoTest {

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
        CreateOrderItemRequest item = new CreateOrderItemRequest(UUID.randomUUID(), 1, BigDecimal.ONE);
        CreateOrderRequest request = new CreateOrderRequest(customerId, List.of(item));
        assertEquals(customerId, request.customerId());
        assertEquals(1, request.items().size());
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

    @Test
    void testOrderEntity() {
        UUID customerId = UUID.randomUUID();
        OrderEntity entity = new OrderEntity(customerId, BigDecimal.TEN, OrderStatus.PENDING);
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(Instant.now());
        entity.setStatus(OrderStatus.CONFIRMED);
        
        assertNotNull(entity.getId());
        assertEquals(customerId, entity.getCustomerId());
        assertEquals(BigDecimal.TEN, entity.getTotalAmount());
        assertEquals(OrderStatus.CONFIRMED, entity.getStatus());
        assertNotNull(entity.getCreatedAt());
        
        OrderItemEntity item = new OrderItemEntity(UUID.randomUUID(), 1, BigDecimal.TEN);
        entity.addItem(item);
        assertEquals(1, entity.getItems().size());
        assertEquals(entity, item.getOrder());
    }

    @Test
    void testOrderItemEntity() {
        UUID productId = UUID.randomUUID();
        OrderItemEntity entity = new OrderItemEntity(productId, 5, BigDecimal.ONE);
        assertEquals(productId, entity.getProductId());
        assertEquals(5, entity.getQuantity());
        assertEquals(BigDecimal.ONE, entity.getUnitPrice());
        
        entity.prePersist();
        assertNotNull(entity.getId());
    }

    @Test
    void testOrderEntityPrePersistAndPreUpdate() throws InterruptedException {
        OrderEntity entity = new OrderEntity(UUID.randomUUID(), BigDecimal.TEN, OrderStatus.PENDING);
        entity.prePersist();
        assertNotNull(entity.getId());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        
        Instant firstUpdate = entity.getUpdatedAt();
        Thread.sleep(10);
        entity.preUpdate();
        assertTrue(entity.getUpdatedAt().isAfter(firstUpdate));
    }
}
