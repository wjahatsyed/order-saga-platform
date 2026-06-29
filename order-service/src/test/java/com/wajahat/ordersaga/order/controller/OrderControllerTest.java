package com.wajahat.ordersaga.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wajahat.ordersaga.common.dto.PageResponse;
import com.wajahat.ordersaga.common.enums.OrderStatus;
import com.wajahat.ordersaga.order.dto.CreateOrderItemRequest;
import com.wajahat.ordersaga.order.dto.CreateOrderRequest;
import com.wajahat.ordersaga.order.dto.OrderResponse;
import com.wajahat.ordersaga.order.exception.GlobalExceptionHandler;
import com.wajahat.ordersaga.order.security.JwtTokenProvider;
import com.wajahat.ordersaga.order.service.OrderService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createsOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(customerId, List.of(
                new CreateOrderItemRequest(UUID.randomUUID(), 1, new BigDecimal("19.99"))
        ));
        OrderResponse response = new OrderResponse(orderId, customerId, new BigDecimal("19.99"), OrderStatus.PENDING, Instant.now());

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void getsOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderResponse response = new OrderResponse(orderId, customerId, new BigDecimal("10.00"), OrderStatus.PENDING, Instant.now());

        when(orderService.getOrder(orderId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId.toString()));
    }

    @Test
    void getsCustomerOrders() throws Exception {
        UUID customerId = UUID.randomUUID();
        PageResponse<OrderResponse> response = new PageResponse<>(List.of(), 0, 20, 0, 0);

        when(orderService.getCustomerOrders(eq(customerId), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void validatesCreateOrderRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(null, List.of());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }
}
