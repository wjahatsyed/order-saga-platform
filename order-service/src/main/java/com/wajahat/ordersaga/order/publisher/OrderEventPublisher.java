package com.wajahat.ordersaga.order.publisher;

import com.wajahat.ordersaga.order.entity.OrderEntity;

public interface OrderEventPublisher {
    void publishOrderCreated(OrderEntity order);
    void publishOrderConfirmed(OrderEntity order);
    void publishOrderCancelled(OrderEntity order, String reason);
}
