package com.wajahat.ordersaga.order.repository;

import com.wajahat.ordersaga.order.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Page<OrderEntity> findByCustomerId(UUID customerId, Pageable pageable);
}
