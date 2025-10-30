package com.raf.service;

import com.raf.dto.WarehouseUpdateRequest;
import com.raf.entity.Location;
import com.raf.entity.StorageWarehouse;
import com.raf.enums.WarehouseStatus;
import com.raf.enums.WarehouseType;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.LocationRepository;
import com.raf.repository.StorageWarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseService {
    private static final String WAREHOUSE_NOT_FOUND_WITH_ID = "Warehouse not found with ID: ";
    
    private final StorageWarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    public StorageWarehouse createWarehouseFromRequest(WarehouseUpdateRequest request) {
        log.info("Creating warehouse from request: {}", request.getWarehouseName());

        if (warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
            throw new DuplicateResourceException("Warehouse with code " + request.getWarehouseCode() + " already exists");
        }

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));

        StorageWarehouse warehouse = new StorageWarehouse();
        warehouse.setWarehouseCode(request.getWarehouseCode());
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setWarehouseType(request.getWarehouseType());
        warehouse.setTotalCapacityKg(request.getTotalCapacityKg());
        warehouse.setAvailableCapacityKg(request.getAvailableCapacityKg());
        warehouse.setStatus(request.getStatus());
        warehouse.setLocation(location);

        return warehouseRepository.save(warehouse);
    }

    public StorageWarehouse createWarehouse(StorageWarehouse warehouse) {
        if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new DuplicateResourceException("Warehouse with code " + warehouse.getWarehouseCode() + " already exists");
        }
        log.info("Creating new warehouse: {}", warehouse.getWarehouseName());
        return warehouseRepository.save(warehouse);
    }
    
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<StorageWarehouse> getWarehousesPaginated(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public StorageWarehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));
    }
    
    @Transactional(readOnly = true)
    public StorageWarehouse getWarehouseByCode(String warehouseCode) {
        return warehouseRepository.findByWarehouseCode(warehouseCode)
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with code: " + warehouseCode));
    }
    
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesByType(WarehouseType type) {
        return warehouseRepository.findByWarehouseType(type);
    }
    
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesByStatus(WarehouseStatus status) {
        return warehouseRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesByLocation(Long locationId) {
        return warehouseRepository.findByLocationId(locationId);
    }
    
    @Transactional(readOnly = true)
    public List<StorageWarehouse> getWarehousesWithAvailableCapacity(BigDecimal minCapacity) {
        return warehouseRepository.findAll().stream()
            .filter(w -> w.getAvailableCapacityKg().compareTo(minCapacity) >= 0)
            .toList();
    }
    
    public StorageWarehouse updateWarehouseFromRequest(Long id, WarehouseUpdateRequest request) {
        log.info("Updating warehouse ID: {} from request", id);

        StorageWarehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));

        if (!warehouse.getWarehouseCode().equals(request.getWarehouseCode()) &&
                warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
            throw new DuplicateResourceException("Warehouse with code " + request.getWarehouseCode() + " already exists");
        }

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));
        warehouse.setWarehouseCode(request.getWarehouseCode());
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setWarehouseType(request.getWarehouseType());
        warehouse.setTotalCapacityKg(request.getTotalCapacityKg());
        warehouse.setAvailableCapacityKg(request.getAvailableCapacityKg());
        warehouse.setStatus(request.getStatus());
        warehouse.setLocation(location);

        return warehouseRepository.save(warehouse);
    }

    public StorageWarehouse updateWarehouse(Long id, StorageWarehouse warehouseDetails) {
        StorageWarehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));
        warehouse.setWarehouseCode(warehouseDetails.getWarehouseCode());
        warehouse.setWarehouseName(warehouseDetails.getWarehouseName());
        warehouse.setWarehouseType(warehouseDetails.getWarehouseType());
        warehouse.setTotalCapacityKg(warehouseDetails.getTotalCapacityKg());
        warehouse.setAvailableCapacityKg(warehouseDetails.getAvailableCapacityKg());
        warehouse.setStatus(warehouseDetails.getStatus());
        warehouse.setLocation(warehouseDetails.getLocation());
        log.info("Updating warehouse ID: {}", id);
        return warehouseRepository.save(warehouse);
    }
    
    public StorageWarehouse updateWarehouseStatus(Long id, WarehouseStatus status) {
        StorageWarehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));
        warehouse.setStatus(status);
        log.info("Updating warehouse ID {} status to: {}", id, status);
        return warehouseRepository.save(warehouse);
    }
    
    public void deleteWarehouse(Long id) {
        StorageWarehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));
        log.info("Deleting warehouse ID: {}", id);
        warehouseRepository.delete(warehouse);
    }
    
    @Transactional(readOnly = true)
    public boolean warehouseCodeExists(String warehouseCode) {
        return warehouseRepository.existsByWarehouseCode(warehouseCode);
    }
    
    @Transactional(readOnly = true)
    public long getTotalWarehouses() {
        return warehouseRepository.count();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalAvailableCapacity() {
        List<StorageWarehouse> warehouses = warehouseRepository.findByStatus(WarehouseStatus.ACTIVE);
        return warehouses.stream()
            .map(StorageWarehouse::getAvailableCapacityKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
