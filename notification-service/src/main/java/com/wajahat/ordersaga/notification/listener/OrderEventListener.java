package com.wajahat.ordersaga.notification.listener;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.OrderCancelledEvent;
import com.wajahat.ordersaga.common.event.OrderConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @KafkaListener(topics = KafkaTopics.ORDER_CONFIRMED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("NOTIFICATION: Order {} has been CONFIRMED for customer {}", 
                event.orderId(), event.customerId());
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CANCELLED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("NOTIFICATION: Order {} has been CANCELLED for customer {}. Reason: {}", 
                event.orderId(), event.customerId(), event.reason());
    }
}
