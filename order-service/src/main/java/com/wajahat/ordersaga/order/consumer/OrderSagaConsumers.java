package com.wajahat.ordersaga.order.consumer;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.InventoryRejectedEvent;
import com.wajahat.ordersaga.common.event.PaymentCompletedEvent;
import com.wajahat.ordersaga.common.event.PaymentFailedEvent;
import com.wajahat.ordersaga.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaConsumers {
    private static final Logger log = LoggerFactory.getLogger(OrderSagaConsumers.class);
    private final OrderService orderService;

    public OrderSagaConsumers(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_REJECTED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleInventoryRejected(InventoryRejectedEvent event) {
        log.info("Received inventory rejected for order: {}", event.orderId());
        orderService.cancelOrder(event.orderId(), event.reason());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED, groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment completed for order: {}", event.orderId());
        orderService.confirmOrder(event.orderId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment failed for order: {}", event.orderId());
        orderService.cancelOrder(event.orderId(), event.reason());
    }
}
