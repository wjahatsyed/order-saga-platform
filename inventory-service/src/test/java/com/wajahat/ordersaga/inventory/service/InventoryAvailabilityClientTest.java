package com.wajahat.ordersaga.inventory.service;

import com.wajahat.ordersaga.inventory.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class InventoryAvailabilityClientTest {

    @Autowired
    private InventoryAvailabilityClient inventoryAvailabilityClient;

    @Test
    void shouldReturnTrueWhenProductIsAvailable() {
        boolean result = inventoryAvailabilityClient.checkAvailability(UUID.randomUUID(), 1);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenInventorySystemThrowsException() {
        // Based on simulation logic: productId starting with 0000 throws exception
        UUID productId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        boolean result = inventoryAvailabilityClient.checkAvailability(productId, 1);
        assertFalse(result);
    }
}
