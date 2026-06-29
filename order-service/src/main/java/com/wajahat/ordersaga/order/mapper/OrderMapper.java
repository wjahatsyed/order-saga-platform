package com.wajahat.ordersaga.order.mapper;

import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
