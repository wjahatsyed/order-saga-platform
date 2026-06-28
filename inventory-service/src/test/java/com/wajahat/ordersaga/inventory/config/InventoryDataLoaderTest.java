package com.wajahat.ordersaga.inventory.config;

import com.wajahat.ordersaga.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryDataLoaderTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Test
    void loadData_ShouldNotSeedExistingProducts() throws Exception {
        when(inventoryRepository.existsById(any())).thenReturn(true);

        CommandLineRunner runner = new InventoryDataLoader(inventoryRepository).loadData();

        runner.run();

        verify(inventoryRepository, never()).save(any());
    }
}
