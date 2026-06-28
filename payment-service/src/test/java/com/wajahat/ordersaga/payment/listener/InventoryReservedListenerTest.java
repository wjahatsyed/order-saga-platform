package com.wajahat.ordersaga.payment.listener;

import com.wajahat.ordersaga.common.constants.KafkaTopics;
import com.wajahat.ordersaga.common.event.InventoryReservedEvent;
import com.wajahat.ordersaga.common.event.PaymentCompletedEvent;
import com.wajahat.ordersaga.common.event.PaymentFailedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryReservedListenerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private InventoryReservedListener listener;

    @BeforeEach
    void setUp() {
        listener = new InventoryReservedListener(kafkaTemplate);
    }

    @Test
    void handleInventoryReserved_ShouldPublishPaymentCompleted() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        InventoryReservedEvent event = new InventoryReservedEvent(
                UUID.randomUUID(), orderId, customerId, Instant.now()
        );

        listener.handleInventoryReserved(event);

        verify(kafkaTemplate).send(eq(KafkaTopics.PAYMENT_COMPLETED), eq(orderId.toString()), any(PaymentCompletedEvent.class));
    }
}
