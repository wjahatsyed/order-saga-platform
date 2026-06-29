package com.wajahat.ordersaga.order.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wajahat.ordersaga.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EntityTest {

    @Test
    void testOrderEntity() {
        UUID customerId = UUID.randomUUID();
        OrderEntity order = new OrderEntity(customerId, BigDecimal.TEN, OrderStatus.PENDING);
        order.setId(UUID.randomUUID());
        
        OrderItemEntity item = new OrderItemEntity(UUID.randomUUID(), 1, BigDecimal.TEN);
        order.addItem(item);
        
        assertEquals(customerId, order.getCustomerId());
        assertEquals(BigDecimal.TEN, order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());
        
        order.setStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void testOrderItemEntity() {
        UUID productId = UUID.randomUUID();
        OrderItemEntity item = new OrderItemEntity(productId, 2, BigDecimal.TEN);
        
        assertEquals(productId, item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(BigDecimal.TEN, item.getUnitPrice());
    }
}
