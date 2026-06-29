package com.wajahat.ordersaga.common.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        String reason,
        Instant occurredAt
) {
}
