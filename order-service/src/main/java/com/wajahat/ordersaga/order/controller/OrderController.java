package com.wajahat.ordersaga.order.controller;

import com.wajahat.ordersaga.common.dto.ApiResponse;
import com.wajahat.ordersaga.common.dto.PageResponse;
import com.wajahat.ordersaga.order.dto.CreateOrderRequest;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders", description = "Order API foundation")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create an order")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(true, "Order accepted", response, Instant.now()));
    }

    @Operation(summary = "Get an order by id")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved", orderService.getOrder(orderId), Instant.now()));
    }

    @Operation(summary = "Get customer orders")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getCustomerOrders(
            @PathVariable("customerId") UUID customerId,
            Pageable pageable
    ) {
        PageResponse<OrderResponse> response = orderService.getCustomerOrders(customerId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer orders retrieved", response, Instant.now()));
    }
}
