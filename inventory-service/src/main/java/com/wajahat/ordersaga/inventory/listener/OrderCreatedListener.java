package com.wajahat.ordersaga.inventory.listener;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.InventoryRejectedEvent;
import com.wajahat.ordersaga.common.event.InventoryReservedEvent;
import com.wajahat.ordersaga.common.event.OrderCreatedEvent;
import com.wajahat.ordersaga.common.event.OrderItemPayload;
import com.wajahat.ordersaga.inventory.entity.InventoryEntity;
import com.wajahat.ordersaga.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing order created event for order: {}", event.orderId());
        
        try {
            for (OrderItemPayload item : event.items()) {
                InventoryEntity inventory = inventoryRepository.findById(item.productId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + item.productId()));
                
                if (inventory.getAvailableQuantity() < item.quantity()) {
                    publishRejected(event.orderId(), event.customerId(), "Insufficient stock for product: " + item.productId());
                    return;
                }
                
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.quantity());
                inventoryRepository.save(inventory);
            }
            
            publishReserved(event.orderId(), event.customerId());
        } catch (Exception e) {
            log.error("Error processing inventory for order: {}", event.orderId(), e);
            publishRejected(event.orderId(), event.customerId(), e.getMessage());
        }
    }

    private void publishReserved(UUID orderId, UUID customerId) {
        InventoryReservedEvent event = new InventoryReservedEvent(
                UUID.randomUUID(),
                orderId,
                customerId,
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, orderId.toString(), event);
    }

    private void publishRejected(UUID orderId, UUID customerId, String reason) {
        InventoryRejectedEvent event = new InventoryRejectedEvent(
                UUID.randomUUID(),
                orderId,
                customerId,
                reason,
                Instant.now()
        );
        kafkaTemplate.send(KafkaTopics.INVENTORY_REJECTED, orderId.toString(), event);
    }
}
