package com.wajahat.ordersaga.order.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wajahat.ordersaga.order.auth.AuthController;
import com.wajahat.ordersaga.order.auth.AuthService;
import com.wajahat.ordersaga.order.auth.LoginRequest;
import com.wajahat.ordersaga.order.controller.OrderController;
import com.wajahat.ordersaga.order.exception.GlobalExceptionHandler;
import com.wajahat.ordersaga.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuthController.class, OrderController.class})
@AutoConfigureMockMvc(addFilters = true)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtTokenProvider.class,
        AuthService.class,
        GlobalExceptionHandler.class
})
@EnableConfigurationProperties(JwtProperties.class)
@TestPropertySource(properties = {
        "security.jwt.secret=order-saga-demo-secret-key-for-jwt-signing-2026",
        "security.jwt.expiration-seconds=3600"
})
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void unauthenticatedOrderApiReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", "11111111-1111-1111-1111-111111111111"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginReturnsTokenThroughSecurityChain() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("customer", "customer123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }
}
