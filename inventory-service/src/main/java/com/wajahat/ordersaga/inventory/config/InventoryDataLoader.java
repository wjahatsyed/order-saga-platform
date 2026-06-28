package com.wajahat.ordersaga.inventory.config;

import com.wajahat.ordersaga.inventory.entity.InventoryEntity;
import com.wajahat.ordersaga.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class InventoryDataLoader {

    private final InventoryRepository inventoryRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Seed some data for testing
            UUID product1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID product2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
            
            if (!inventoryRepository.existsById(product1)) {
                inventoryRepository.save(new InventoryEntity(product1, 100));
            }
            if (!inventoryRepository.existsById(product2)) {
                inventoryRepository.save(new InventoryEntity(product2, 50));
            }
        };
    }
}
