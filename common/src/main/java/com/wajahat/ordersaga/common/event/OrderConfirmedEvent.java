package com.wajahat.ordersaga.common.event;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        Instant occurredAt
) {
}
