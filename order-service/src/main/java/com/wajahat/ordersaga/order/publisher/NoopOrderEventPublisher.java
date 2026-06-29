package com.wajahat.ordersaga.order.publisher;

import com.wajahat.ordersaga.order.entity.OrderEntity;
import org.springframework.stereotype.Component;

//@Component
public class NoopOrderEventPublisher implements OrderEventPublisher {

    @Override
    public void publishOrderCreated(OrderEntity order) {
        // Kafka publishing will be wired in the SAGA implementation phase.
    }

    @Override
    public void publishOrderConfirmed(OrderEntity order) {
    }

    @Override
    public void publishOrderCancelled(OrderEntity order, String reason) {
    }
}
