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
    private final com.wajahat.ordersaga.payment.service.PaymentGatewayClient paymentGatewayClient;

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "${spring.kafka.consumer.group-id}")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Processing payment for order: {}", event.orderId());
        
        BigDecimal amount = BigDecimal.valueOf(100.0); // Dummy amount for simulation
        
        boolean success = paymentGatewayClient.chargePayment(event.customerId(), amount);
        
        if (success) {
            publishPaymentCompleted(event.orderId(), event.customerId(), amount);
        } else {
            publishPaymentFailed(event.orderId(), event.customerId(), amount, "Payment failed via gateway");
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
