package com.wajahat.ordersaga.order.service;

import com.wajahat.ordersaga.common.dto.PageResponse;
import com.wajahat.ordersaga.order.dto.CreateOrderRequest;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrder(UUID orderId);

    PageResponse<OrderResponse> getCustomerOrders(UUID customerId, Pageable pageable);

    void confirmOrder(UUID orderId);

    void cancelOrder(UUID orderId, String reason);
}
