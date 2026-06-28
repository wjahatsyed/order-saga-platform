package com.wajahat.ordersaga.order.publisher;

import com.wajahat.ordersaga.order.entity.OrderEntity;

public interface OrderEventPublisher {
    void publishOrderCreated(OrderEntity order);
}
