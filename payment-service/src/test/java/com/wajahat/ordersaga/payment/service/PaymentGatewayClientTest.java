package com.wajahat.ordersaga.payment.service;

import com.wajahat.ordersaga.payment.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class PaymentGatewayClientTest {

    @Autowired
    private PaymentGatewayClient paymentGatewayClient;

    @Test
    void shouldReturnTrueWhenAmountIsPositive() {
        boolean result = paymentGatewayClient.chargePayment(UUID.randomUUID(), BigDecimal.TEN);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenAmountIsZeroOrNegative() {
        boolean result = paymentGatewayClient.chargePayment(UUID.randomUUID(), BigDecimal.ZERO);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenGatewayThrowsException() {
        // Based on simulation logic in PaymentGatewayClient: amount 999 throws exception
        boolean result = paymentGatewayClient.chargePayment(UUID.randomUUID(), new BigDecimal("999"));
        assertFalse(result);
    }
}
