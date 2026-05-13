package com.raf.service;

import com.raf.dto.InventoryRequest;
import com.raf.entity.CropType;
import com.raf.entity.Inventory;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.User;
import com.raf.enums.InventoryStatus;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.InventoryRepository;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StorageWarehouseRepository warehouseRepository;
    @Mock
    private CropTypeRepository cropTypeRepository;
    @Mock
    private WarehouseAccessRepository warehouseAccessRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private InventoryService inventoryService;

    private User farmer;
    private User storekeeper;
    private StorageWarehouse warehouse;
    private CropType cropType;
    private InventoryRequest validRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(inventoryService, "entityManager", entityManager);

        farmer = new User();
        farmer.setId(1L);

        storekeeper = new User();
        storekeeper.setId(2L);

        warehouse = new StorageWarehouse();
        warehouse.setId(1L);
        warehouse.setTotalCapacityKg(new BigDecimal("1000.00"));
        warehouse.setAvailableCapacityKg(new BigDecimal("1000.00"));

        cropType = new CropType();
        cropType.setId(1L);
        cropType.setCropName("Maize");

        validRequest = new InventoryRequest();
        validRequest.setFarmerId(1L);
        validRequest.setWarehouseId(1L);
        validRequest.setCropTypeId(1L);
        validRequest.setStorekeeperId(2L);
        validRequest.setQuantityKg(new BigDecimal("100.00"));
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        Mockito.lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(farmer));
        Mockito.lenient().when(userRepository.findById(2L)).thenReturn(Optional.of(storekeeper));
        Mockito.lenient().when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        Mockito.lenient().when(cropTypeRepository.findById(1L)).thenReturn(Optional.of(cropType));
        Mockito.lenient().when(inventoryRepository.existsByInventoryCode(any(String.class))).thenReturn(false);
        Mockito.lenient().when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventory result = inventoryService.createInventoryFromRequest(validRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getQuantityKg());
        assertEquals(new BigDecimal("100.00"), result.getRemainingQuantityKg());
        assertEquals(InventoryStatus.STORED, result.getStatus());
        assertEquals(new BigDecimal("900.00"), warehouse.getAvailableCapacityKg()); // Check capacity reduction
    }

    @Test
    void shouldThrowExceptionWhenSellingMoreThanRemaining() {
        Inventory existingInventory = new Inventory();
        existingInventory.setId(1L);
        existingInventory.setQuantityKg(new BigDecimal("100.00"));
        existingInventory.setRemainingQuantityKg(new BigDecimal("100.00"));

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.reduceInventoryQuantity(1L, new BigDecimal("150.00")); // Exceeds remaining
        });

        assertTrue(exception.getMessage().contains("Insufficient quantity"));
    }

    @Test
    void shouldThrowExceptionWhenFarmerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.createInventoryFromRequest(validRequest);
        });
    }

    @Test
    void shouldUpdateInventoryStatus() {
        Inventory existingInventory = new Inventory();
        existingInventory.setId(1L);
        existingInventory.setQuantityKg(new BigDecimal("100.00"));
        existingInventory.setRemainingQuantityKg(new BigDecimal("100.00"));
        existingInventory.setStatus(InventoryStatus.STORED);
        existingInventory.setWarehouse(warehouse);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When 100 is sold, remaining becomes 0 -> SOLD
        Inventory result = inventoryService.reduceInventoryQuantity(1L, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("0.00"), result.getRemainingQuantityKg());
        assertEquals(InventoryStatus.SOLD, result.getStatus());
    }
}
