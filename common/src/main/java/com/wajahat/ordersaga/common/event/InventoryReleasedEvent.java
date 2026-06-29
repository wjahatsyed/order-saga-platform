package com.wajahat.ordersaga.common.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryReleasedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        Instant occurredAt
) {
}
