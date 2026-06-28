package com.wajahat.ordersaga.common.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class KafkaTopicsTest {

    @Test
    void exposesExpectedTopicNames() {
        assertEquals("order.created", KafkaTopics.ORDER_CREATED);
        assertEquals("inventory.reserved", KafkaTopics.INVENTORY_RESERVED);
        assertEquals("inventory.rejected", KafkaTopics.INVENTORY_REJECTED);
        assertEquals("payment.completed", KafkaTopics.PAYMENT_COMPLETED);
        assertEquals("payment.failed", KafkaTopics.PAYMENT_FAILED);
        assertEquals("order.confirmed", KafkaTopics.ORDER_CONFIRMED);
        assertEquals("order.cancelled", KafkaTopics.ORDER_CANCELLED);
        assertEquals("inventory.released", KafkaTopics.INVENTORY_RELEASED);
        assertEquals("payment.refunded", KafkaTopics.PAYMENT_REFUNDED);
    }
}
