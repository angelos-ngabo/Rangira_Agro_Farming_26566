package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.Inventory;
import com.raf.Rangira.Agro.Farming.enums.InventoryStatus;
import com.raf.Rangira.Agro.Farming.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Inventory Service
 * Core business logic for crop storage management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    // CREATE
    public Inventory createInventory(Inventory inventory) {
        log.info("Creating inventory: {} for farmer {}", 
                inventory.getInventoryCode(), inventory.getFarmer().getUserCode());
        
        if (inventoryRepository.existsByInventoryCode(inventory.getInventoryCode())) {
            throw new RuntimeException("Inventory code already exists");
        }
        
        // Set initial remaining quantity equal to total quantity
        inventory.setRemainingQuantityKg(inventory.getQuantityKg());
        inventory.setStatus(InventoryStatus.STORED);
        inventory.setStorageDate(LocalDate.now());
        
        return inventoryRepository.save(inventory);
    }
    
    // READ
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
    }
    
    public Inventory getInventoryByCode(String code) {
        return inventoryRepository.findByInventoryCode(code)
                .orElseThrow(() -> new RuntimeException("Inventory not found with code: " + code));
    }
    
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }
    
    public List<Inventory> getInventoriesByFarmer(Long farmerId) {
        return inventoryRepository.findByFarmerId(farmerId);
    }
    
    public List<Inventory> getInventoriesByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }
    
    public List<Inventory> getInventoriesByCropType(Long cropTypeId) {
        return inventoryRepository.findByCropTypeId(cropTypeId);
    }
    
    public List<Inventory> getInventoriesByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }
    
    public List<Inventory> getInventoriesByProvinceCode(String provinceCode) {
        return inventoryRepository.findInventoriesByProvinceCode(provinceCode);
    }
    
    public Page<Inventory> getInventoriesPaginated(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }
    
    // UPDATE
    public Inventory updateInventory(Long id, Inventory inventoryDetails) {
        log.info("Updating inventory with id: {}", id);
        
        Inventory inventory = getInventoryById(id);
        
        inventory.setQualityGrade(inventoryDetails.getQualityGrade());
        inventory.setExpectedWithdrawalDate(inventoryDetails.getExpectedWithdrawalDate());
        inventory.setNotes(inventoryDetails.getNotes());
        
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Reduce inventory quantity (when sold or withdrawn)
     */
    public Inventory reduceInventoryQuantity(Long id, BigDecimal soldQuantity) {
        log.info("Reducing inventory {} by {} kg", id, soldQuantity);
        
        Inventory inventory = getInventoryById(id);
        
        if (inventory.getRemainingQuantityKg().compareTo(soldQuantity) < 0) {
            throw new RuntimeException("Insufficient quantity available. Available: " + 
                    inventory.getRemainingQuantityKg() + " kg");
        }
        
        BigDecimal newRemaining = inventory.getRemainingQuantityKg().subtract(soldQuantity);
        inventory.setRemainingQuantityKg(newRemaining);
        
        // Update status based on remaining quantity
        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            inventory.setStatus(InventoryStatus.SOLD);
        } else {
            inventory.setStatus(InventoryStatus.PARTIALLY_SOLD);
        }
        
        return inventoryRepository.save(inventory);
    }
    
    /**
     * Mark inventory as withdrawn
     */
    public Inventory withdrawInventory(Long id) {
        log.info("Marking inventory {} as withdrawn", id);
        
        Inventory inventory = getInventoryById(id);
        inventory.setStatus(InventoryStatus.WITHDRAWN);
        inventory.setRemainingQuantityKg(BigDecimal.ZERO);
        
        return inventoryRepository.save(inventory);
    }
    
    // DELETE
    public void deleteInventory(Long id) {
        log.info("Deleting inventory with id: {}", id);
        Inventory inventory = getInventoryById(id);
        inventoryRepository.delete(inventory);
    }
    
    // Business Logic
    public BigDecimal getTotalStoredQuantityInWarehouse(Long warehouseId) {
        BigDecimal total = inventoryRepository.getTotalStoredQuantityInWarehouse(warehouseId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public long countActiveInventoriesByFarmer(Long farmerId) {
        return inventoryRepository.countActiveInventoriesByFarmer(farmerId);
    }
    
    public List<Inventory> getAvailableInventoryInWarehouse(Long warehouseId, BigDecimal minQuantity) {
        return inventoryRepository.findAvailableInventoryInWarehouse(
                warehouseId, InventoryStatus.STORED, minQuantity);
    }
}

