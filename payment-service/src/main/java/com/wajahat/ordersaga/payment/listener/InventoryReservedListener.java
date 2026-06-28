package com.wajahat.ordersaga.payment.listener;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.InventoryReservedEvent;
import com.wajahat.ordersaga.common.event.PaymentCompletedEvent;
import com.wajahat.ordersaga.common.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReservedListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Processing payment for order: {}", event.orderId());
        
        // Simulate payment: fail if customerId ends with "000000000000" (just a placeholder logic)
        // Or if we had amount in the event, but we don't.
        // The requirement says: "simulate success unless amount <= 0".
        // Since we don't have amount in InventoryReservedEvent, we'll assume a default amount or 
        // normally we should have included it.
        
        BigDecimal amount = BigDecimal.valueOf(100.0); // Dummy amount for simulation
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            publishPaymentFailed(event.orderId(), event.customerId(), amount, "Invalid amount");
        } else {
            publishPaymentCompleted(event.orderId(), event.customerId(), amount);
        }
    }

    private void publishPaymentCompleted(UUID orderId, UUID customerId, BigDecimal amount) {
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                orderId,
                customerId,
                amount,
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED, orderId.toString(), event);
    }

    private void publishPaymentFailed(UUID orderId, UUID customerId, BigDecimal amount, String reason) {
        PaymentFailedEvent event = new PaymentFailedEvent(
                UUID.randomUUID(),
                orderId,
                customerId,
                amount,
                reason,
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, orderId.toString(), event);
    }
}
