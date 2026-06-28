package com.wajahat.ordersaga.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wajahat.ordersaga.common.enums.OrderStatus;
import com.wajahat.ordersaga.common.exception.BusinessException;
import com.wajahat.ordersaga.order.dto.CreateOrderItemRequest;
import com.wajahat.ordersaga.order.dto.CreateOrderRequest;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import com.wajahat.ordersaga.order.mapper.OrderMapper;
import com.wajahat.ordersaga.order.publisher.OrderEventPublisher;
import com.wajahat.ordersaga.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createsPendingOrder() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-06-28T10:00:00Z");
        CreateOrderRequest request = new CreateOrderRequest(customerId, List.of(
                new CreateOrderItemRequest(UUID.randomUUID(), 2, new BigDecimal("12.50")),
                new CreateOrderItemRequest(UUID.randomUUID(), 1, new BigDecimal("5.00"))
        ));
        OrderEntity savedOrder = new OrderEntity(customerId, new BigDecimal("30.00"), OrderStatus.PENDING);
        savedOrder.setId(orderId);
        savedOrder.setCreatedAt(createdAt);
        OrderResponse expected = new OrderResponse(orderId, customerId, new BigDecimal("30.00"), OrderStatus.PENDING, createdAt);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(expected);

        OrderResponse response = orderService.createOrder(request);

        assertEquals(expected, response);
        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderEventPublisher).publishOrderCreated(savedOrder);
    }

    @Test
    void rejectsEmptyOrder() {
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), List.of());

        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.createOrder(request));

        assertEquals("ORDER_ITEMS_REQUIRED", exception.getErrorCode());
    }
}
