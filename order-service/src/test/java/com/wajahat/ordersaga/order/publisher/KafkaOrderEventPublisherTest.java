package com.wajahat.ordersaga.order.publisher;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.OrderCreatedEvent;
import com.wajahat.ordersaga.common.enums.OrderStatus;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import com.wajahat.ordersaga.order.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaOrderEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaOrderEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new KafkaOrderEventPublisher(kafkaTemplate);
    }

    @Test
    void publishOrderCreated_ShouldSendKafkaMessage() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderEntity order = new OrderEntity(customerId, new BigDecimal("100.00"), OrderStatus.PENDING);
        order.setId(orderId);
        order.addItem(new OrderItemEntity(UUID.randomUUID(), 2, new BigDecimal("50.00")));

        publisher.publishOrderCreated(order);

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.ORDER_CREATED), eq(orderId.toString()), eventCaptor.capture());

        OrderCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(orderId, capturedEvent.orderId());
        assertEquals(customerId, capturedEvent.customerId());
        assertEquals(1, capturedEvent.items().size());
    }
}
