package com.wajahat.ordersaga.payment.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class PaymentGatewayClient {

    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "fallbackChargePayment")
    @Retry(name = "paymentGateway")
    public boolean chargePayment(UUID customerId, BigDecimal amount) {
        log.info("Attempting to charge payment for customer: {} amount: {}", customerId, amount);
        
        // Simulate external call
        if (amount.compareTo(new BigDecimal("999")) == 0) {
            throw new RuntimeException("Payment Gateway is down!");
        }
        
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean fallbackChargePayment(UUID customerId, BigDecimal amount, Throwable t) {
        log.error("Fallback for chargePayment: customerId={}, amount={}, error={}", customerId, amount, t.getMessage());
        return false;
    }
}
