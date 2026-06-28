package com.wajahat.ordersaga.inventory.listener;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.InventoryRejectedEvent;
import com.wajahat.ordersaga.common.event.InventoryReservedEvent;
import com.wajahat.ordersaga.common.event.OrderCreatedEvent;
import com.wajahat.ordersaga.common.event.OrderItemPayload;
import com.wajahat.ordersaga.inventory.entity.InventoryEntity;
import com.wajahat.ordersaga.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCreatedListenerTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private com.wajahat.ordersaga.inventory.service.InventoryAvailabilityClient inventoryAvailabilityClient;

    private OrderCreatedListener listener;

    @BeforeEach
    void setUp() {
        listener = new OrderCreatedListener(inventoryRepository, kafkaTemplate, inventoryAvailabilityClient);
    }

    @Test
    void handleOrderCreated_Success_ShouldPublishReserved() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), orderId, customerId,
                List.of(new OrderItemPayload(productId, 2, new BigDecimal("10.0"))),
                new BigDecimal("20.0"), Instant.now()
        );
        InventoryEntity inventory = new InventoryEntity(productId, 10);

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryAvailabilityClient.checkAvailability(any(), anyInt())).thenReturn(true);

        listener.handleOrderCreated(event);

        assertEquals(8, inventory.getAvailableQuantity());
        verify(inventoryRepository).save(inventory);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_RESERVED), eq(orderId.toString()), any(InventoryReservedEvent.class));
    }

    @Test
    void handleOrderCreated_InsufficientStock_ShouldPublishRejected() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), orderId, customerId,
                List.of(new OrderItemPayload(productId, 20, new BigDecimal("10.0"))),
                new BigDecimal("200.0"), Instant.now()
        );
        InventoryEntity inventory = new InventoryEntity(productId, 10);

        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryAvailabilityClient.checkAvailability(any(), anyInt())).thenReturn(true);

        listener.handleOrderCreated(event);

        verify(inventoryRepository, never()).save(any());
        ArgumentCaptor<InventoryRejectedEvent> captor = ArgumentCaptor.forClass(InventoryRejectedEvent.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_REJECTED), eq(orderId.toString()), captor.capture());
        assertEquals("Insufficient stock for product: " + productId, captor.getValue().reason());
    }

    @Test
    void handleOrderCreated_ExternalAvailabilityRejected_ShouldPublishRejected() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), orderId, customerId,
                List.of(new OrderItemPayload(productId, 2, new BigDecimal("10.0"))),
                new BigDecimal("20.0"), Instant.now()
        );

        when(inventoryAvailabilityClient.checkAvailability(productId, 2)).thenReturn(false);

        listener.handleOrderCreated(event);

        verify(inventoryRepository, never()).findById(any());
        verify(inventoryRepository, never()).save(any());
        ArgumentCaptor<InventoryRejectedEvent> captor = ArgumentCaptor.forClass(InventoryRejectedEvent.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_REJECTED), eq(orderId.toString()), captor.capture());
        assertEquals("External inventory check failed for product: " + productId, captor.getValue().reason());
    }
}
