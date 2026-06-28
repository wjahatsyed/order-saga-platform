package com.wajahat.ordersaga.order.service;

import com.wajahat.ordersaga.common.dto.PageResponse;
import com.wajahat.ordersaga.common.enums.OrderStatus;
import com.wajahat.ordersaga.common.exception.BusinessException;
import com.wajahat.ordersaga.common.exception.ResourceNotFoundException;
import com.wajahat.ordersaga.order.dto.CreateOrderItemRequest;
import com.wajahat.ordersaga.order.dto.CreateOrderRequest;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import com.wajahat.ordersaga.order.entity.OrderItemEntity;
import com.wajahat.ordersaga.order.mapper.OrderMapper;
import com.wajahat.ordersaga.order.publisher.OrderEventPublisher;
import com.wajahat.ordersaga.order.repository.OrderRepository;
import com.wajahat.ordersaga.order.config.CacheConstants;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final String EMPTY_ORDER = "ORDER_ITEMS_REQUIRED";

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            OrderEventPublisher orderEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException(EMPTY_ORDER, "Order must contain at least one item");
        }

        BigDecimal totalAmount = calculateTotal(request.items());
        OrderEntity order = new OrderEntity(request.customerId(), totalAmount, OrderStatus.PENDING);
        request.items().forEach(item -> order.addItem(new OrderItemEntity(
                item.productId(),
                item.quantity(),
                item.unitPrice()
        )));

        OrderEntity savedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(savedOrder);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.ORDERS_CACHE, key = "#a0")
    public OrderResponse getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.CUSTOMER_ORDERS_CACHE, key = "#a0.toString() + #a1.pageNumber")
    public PageResponse<OrderResponse> getCustomerOrders(UUID customerId, Pageable pageable) {
        Page<OrderResponse> page = orderRepository.findByCustomerId(customerId, pageable)
                .map(orderMapper::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.ORDERS_CACHE, key = "#a0")
    public void confirmOrder(UUID orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            orderEventPublisher.publishOrderConfirmed(order);
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.ORDERS_CACHE, key = "#a0")
    public void cancelOrder(UUID orderId, String reason) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            orderEventPublisher.publishOrderCancelled(order, reason);
        });
    }

    private BigDecimal calculateTotal(List<CreateOrderItemRequest> items) {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
