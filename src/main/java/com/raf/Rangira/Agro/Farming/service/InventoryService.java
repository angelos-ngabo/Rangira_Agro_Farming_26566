package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.dto.InventoryRequest;
import com.raf.Rangira.Agro.Farming.entity.CropType;
import com.raf.Rangira.Agro.Farming.entity.Inventory;
import com.raf.Rangira.Agro.Farming.entity.StorageWarehouse;
import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.enums.InventoryStatus;
import com.raf.Rangira.Agro.Farming.repository.CropTypeRepository;
import com.raf.Rangira.Agro.Farming.repository.InventoryRepository;
import com.raf.Rangira.Agro.Farming.repository.StorageWarehouseRepository;
import com.raf.Rangira.Agro.Farming.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final StorageWarehouseRepository warehouseRepository;
    private final CropTypeRepository cropTypeRepository;
    
    // CREATE from DTO (accepts IDs)
    public Inventory createInventoryFromRequest(InventoryRequest request) {
        log.info("Creating inventory from request: {}", request.getInventoryCode());
        
        if (inventoryRepository.existsByInventoryCode(request.getInventoryCode())) {
            throw new RuntimeException("Inventory code already exists: " + request.getInventoryCode());
        }
        
        User farmer = userRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + request.getFarmerId()));
        
        StorageWarehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + request.getWarehouseId()));
        
        CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with ID: " + request.getCropTypeId()));
        
        User storekeeper = userRepository.findById(request.getStorekeeperId())
                .orElseThrow(() -> new RuntimeException("Storekeeper not found with ID: " + request.getStorekeeperId()));
        
        Inventory inventory = new Inventory();
        inventory.setInventoryCode(request.getInventoryCode());
        inventory.setFarmer(farmer);
        inventory.setWarehouse(warehouse);
        inventory.setCropType(cropType);
        inventory.setStorekeeper(storekeeper);
        inventory.setQuantityKg(request.getQuantityKg());
        inventory.setRemainingQuantityKg(request.getQuantityKg());
        inventory.setQualityGrade(request.getQualityGrade());
        inventory.setStorageDate(request.getStorageDate());
        inventory.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
        inventory.setStatus(InventoryStatus.STORED);
        inventory.setNotes(request.getNotes());
        
        return inventoryRepository.save(inventory);
    }
    
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
    
    public List<Inventory> getInventoriesByLocationCode(String locationCode) {
        return inventoryRepository.findInventoriesByLocationCode(locationCode);
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

