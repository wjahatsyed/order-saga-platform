package com.wajahat.ordersaga.order.publisher;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.OrderCancelledEvent;
import com.wajahat.ordersaga.common.event.OrderConfirmedEvent;
import com.wajahat.ordersaga.common.event.OrderCreatedEvent;
import com.wajahat.ordersaga.common.event.OrderItemPayload;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(OrderEntity order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getCustomerId(),
                order.getItems().stream()
                        .map(item -> new OrderItemPayload(item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                        .collect(Collectors.toList()),
                order.getTotalAmount(),
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, order.getId().toString(), event);
    }

    @Override
    public void publishOrderConfirmed(OrderEntity order) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getCustomerId(),
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.ORDER_CONFIRMED, order.getId().toString(), event);
    }

    @Override
    public void publishOrderCancelled(OrderEntity order, String reason) {
        OrderCancelledEvent event = new OrderCancelledEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getCustomerId(),
                reason,
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, order.getId().toString(), event);
    }
}
