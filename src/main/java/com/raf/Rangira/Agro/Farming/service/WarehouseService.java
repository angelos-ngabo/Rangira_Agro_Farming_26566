package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.StorageWarehouse;
import com.raf.Rangira.Agro.Farming.enums.WarehouseStatus;
import com.raf.Rangira.Agro.Farming.enums.WarehouseType;
import com.raf.Rangira.Agro.Farming.exception.DuplicateResourceException;
import com.raf.Rangira.Agro.Farming.exception.ResourceNotFoundException;
import com.raf.Rangira.Agro.Farming.repository.StorageWarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Warehouse Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseService {
    
    private final StorageWarehouseRepository warehouseRepository;
    
    /**
     * Create a new warehouse
     */
    public StorageWarehouse createWarehouse(StorageWarehouse warehouse) {
        // Check if warehouse code already exists
        if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new DuplicateResourceException("Warehouse with code " + warehouse.getWarehouseCode() + " already exists");
        }
        
        log.info("Creating new warehouse: {}", warehouse.getWarehouseName());
        return warehouseRepository.save(warehouse);
    }
    
    /**
     * Get all warehouses
     */
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    /**
     * Get warehouses with pagination
     */
    @Transactional(readOnly = true)
    public Page<StorageWarehouse> getWarehousesPaginated(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }
    
    /**
     * Get warehouse by ID
     */
    @Transactional(readOnly = true)
    public StorageWarehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + id));
    }
    
    /**
     * Get warehouse by code
     */
    @Transactional(readOnly = true)
    public StorageWarehouse getWarehouseByCode(String warehouseCode) {
        return warehouseRepository.findByWarehouseCode(warehouseCode)
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with code: " + warehouseCode));
    }
    
    /**
     * Get warehouses by type
     */
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesByType(WarehouseType type) {
        return warehouseRepository.findByWarehouseType(type);
    }
    
    /**
     * Get warehouses by status
     */
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesByStatus(WarehouseStatus status) {
        return warehouseRepository.findByStatus(status);
    }
    
    /**
     * Get warehouses by village
     */
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesByVillage(Long villageId) {
        return warehouseRepository.findByVillageId(villageId);
    }
    
    /**
     * Get warehouses with available capacity
     */
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesWithAvailableCapacity(BigDecimal minCapacity) {
        return warehouseRepository.findAll().stream()
            .filter(w -> w.getAvailableCapacityKg().compareTo(minCapacity) >= 0)
            .toList();
    }
    
    /**
     * Update warehouse
     */
    public StorageWarehouse updateWarehouse(Long id, StorageWarehouse warehouseDetails) {
        StorageWarehouse warehouse = getWarehouseById(id);
        
        warehouse.setWarehouseName(warehouseDetails.getWarehouseName());
        warehouse.setWarehouseType(warehouseDetails.getWarehouseType());
        warehouse.setTotalCapacityKg(warehouseDetails.getTotalCapacityKg());
        warehouse.setAvailableCapacityKg(warehouseDetails.getAvailableCapacityKg());
        warehouse.setStatus(warehouseDetails.getStatus());
        
        log.info("Updating warehouse ID: {}", id);
        return warehouseRepository.save(warehouse);
    }
    
    /**
     * Update warehouse status
     */
    public StorageWarehouse updateWarehouseStatus(Long id, WarehouseStatus status) {
        StorageWarehouse warehouse = getWarehouseById(id);
        warehouse.setStatus(status);
        
        log.info("Updating warehouse ID {} status to: {}", id, status);
        return warehouseRepository.save(warehouse);
    }
    
    /**
     * Delete warehouse
     */
    public void deleteWarehouse(Long id) {
        StorageWarehouse warehouse = getWarehouseById(id);
        log.info("Deleting warehouse ID: {}", id);
        warehouseRepository.delete(warehouse);
    }
    
    /**
     * Check if warehouse code exists
     */
    @Transactional(readOnly = true)
    public boolean warehouseCodeExists(String warehouseCode) {
        return warehouseRepository.existsByWarehouseCode(warehouseCode);
    }
    
    /**
     * Get total number of warehouses
     */
    @Transactional(readOnly = true)
    public long getTotalWarehouses() {
        return warehouseRepository.count();
    }
    
    /**
     * Get total available capacity across all warehouses
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAvailableCapacity() {
        List<StorageWarehouse> warehouses = warehouseRepository.findByStatus(WarehouseStatus.ACTIVE);
        return warehouses.stream()
            .map(StorageWarehouse::getAvailableCapacityKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

