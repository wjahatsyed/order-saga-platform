package com.wajahat.ordersaga.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        List<OrderItemPayload> items,
        BigDecimal totalAmount,
        Instant occurredAt
) {
}
