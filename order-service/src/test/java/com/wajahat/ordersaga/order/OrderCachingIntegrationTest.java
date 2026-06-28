package com.wajahat.ordersaga.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wajahat.ordersaga.common.enums.OrderStatus;
import com.wajahat.ordersaga.order.config.CacheConstants;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import com.wajahat.ordersaga.order.mapper.OrderMapper;
import com.wajahat.ordersaga.order.repository.OrderRepository;
import com.wajahat.ordersaga.order.service.OrderService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class OrderCachingIntegrationTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderMapper orderMapper;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void getOrder_ShouldCacheResult() {
        UUID orderId = UUID.randomUUID();
        OrderEntity orderEntity = new OrderEntity(UUID.randomUUID(), new BigDecimal("100.00"), OrderStatus.PENDING);
        orderEntity.setId(orderId);
        OrderResponse response = new OrderResponse(orderId, orderEntity.getCustomerId(), orderEntity.getTotalAmount(), orderEntity.getStatus(), Instant.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toResponse(orderEntity)).thenReturn(response);

        // First call - should call repository
        OrderResponse result1 = orderService.getOrder(orderId);
        assertEquals(response, result1);

        // Second call - should return from cache
        OrderResponse result2 = orderService.getOrder(orderId);
        assertEquals(response, result2);

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void confirmOrder_ShouldEvictCache() {
        UUID orderId = UUID.randomUUID();
        OrderEntity orderEntity = new OrderEntity(UUID.randomUUID(), new BigDecimal("100.00"), OrderStatus.PENDING);
        orderEntity.setId(orderId);
        OrderResponse response = new OrderResponse(orderId, orderEntity.getCustomerId(), orderEntity.getTotalAmount(), orderEntity.getStatus(), Instant.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toResponse(orderEntity)).thenReturn(response);

        // Populate cache
        orderService.getOrder(orderId);
        
        // Evict cache via service call
        orderService.confirmOrder(orderId);

        // Third call - should call repository again
        orderService.getOrder(orderId);

        verify(orderRepository, times(3)).findById(orderId);
    }
}
