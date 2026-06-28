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
import java.util.Optional;
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

    @Test
    void confirmOrder_ShouldUpdateStatusAndPublishEvent() {
        UUID orderId = UUID.randomUUID();
        OrderEntity order = new OrderEntity(UUID.randomUUID(), new BigDecimal("100.00"), OrderStatus.PENDING);
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.confirmOrder(orderId);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository).save(order);
        verify(orderEventPublisher).publishOrderConfirmed(order);
    }

    @Test
    void cancelOrder_ShouldUpdateStatusAndPublishEvent() {
        UUID orderId = UUID.randomUUID();
        OrderEntity order = new OrderEntity(UUID.randomUUID(), new BigDecimal("100.00"), OrderStatus.PENDING);
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId, "Test reason");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
        verify(orderEventPublisher).publishOrderCancelled(order, "Test reason");
    }

    @Test
    void getOrder_ShouldReturnResponse() {
        UUID orderId = UUID.randomUUID();
        OrderEntity order = new OrderEntity(UUID.randomUUID(), new BigDecimal("100.00"), OrderStatus.PENDING);
        order.setId(orderId);
        OrderResponse expected = new OrderResponse(orderId, order.getCustomerId(), order.getTotalAmount(), OrderStatus.PENDING, Instant.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(expected);

        OrderResponse response = orderService.getOrder(orderId);

        assertEquals(expected, response);
    }

    @Test
    void getOrder_ShouldThrowException_WhenNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(com.wajahat.ordersaga.common.exception.ResourceNotFoundException.class, () -> orderService.getOrder(orderId));
    }

    @Test
    void getCustomerOrders_ShouldReturnPageResponse() {
        UUID customerId = UUID.randomUUID();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        OrderEntity order = new OrderEntity(customerId, new BigDecimal("100.00"), OrderStatus.PENDING);
        order.setId(UUID.randomUUID());
        org.springframework.data.domain.Page<OrderEntity> page = new org.springframework.data.domain.PageImpl<>(List.of(order));

        when(orderRepository.findByCustomerId(customerId, pageable)).thenReturn(page);
        when(orderMapper.toResponse(order)).thenReturn(new OrderResponse(order.getId(), customerId, order.getTotalAmount(), OrderStatus.PENDING, Instant.now()));

        com.wajahat.ordersaga.common.dto.PageResponse<OrderResponse> response = orderService.getCustomerOrders(customerId, pageable);

        assertEquals(1, response.content().size());
        assertEquals(0, response.page());
        assertEquals(1, response.totalElements());
    }
}
