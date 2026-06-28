package com.wajahat.ordersaga.order.dto;

import com.wajahat.ordersaga.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {
}
