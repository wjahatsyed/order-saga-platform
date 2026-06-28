package com.wajahat.ordersaga.common.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryRejectedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        String reason,
        Instant occurredAt
) {
}
