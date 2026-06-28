package com.wajahat.ordersaga.inventory.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class InventoryAvailabilityClient {

    @CircuitBreaker(name = "inventoryAvailability", fallbackMethod = "fallbackCheckAvailability")
    @Retry(name = "inventoryAvailability")
    public boolean checkAvailability(UUID productId, Integer quantity) {
        log.info("Checking availability for product: {} quantity: {}", productId, quantity);
        
        // Simulate external call to a warehouse system
        if (productId.toString().startsWith("0000")) {
            throw new RuntimeException("Inventory Warehouse system is down!");
        }
        
        return true; // Assume available for simulation
    }

    public boolean fallbackCheckAvailability(UUID productId, Integer quantity, Throwable t) {
        log.error("Fallback for checkAvailability: productId={}, quantity={}, error={}", productId, quantity, t.getMessage());
        return false;
    }
}
