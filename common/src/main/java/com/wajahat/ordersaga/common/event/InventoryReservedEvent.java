package com.wajahat.ordersaga.common.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        Instant occurredAt
) {
}
