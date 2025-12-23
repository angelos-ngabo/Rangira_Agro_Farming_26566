package com.raf.service;

import com.raf.dto.WarehouseCreateRequest;
import com.raf.dto.WarehouseUpdateRequest;
import com.raf.entity.CropType;
import com.raf.entity.Location;
import com.raf.entity.StorageWarehouse;
import com.raf.enums.WarehouseStatus;
import com.raf.enums.WarehouseType;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.LocationRepository;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.WarehouseAccessRepository;
import com.raf.enums.AccessLevel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
private final CropTypeRepository cropTypeRepository;
private final WarehouseAccessRepository warehouseAccessRepository;
@Lazy
private final NotificationService notificationService;

@PersistenceContext
private EntityManager entityManager;

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

StorageWarehouse savedWarehouse = warehouseRepository.save(warehouse);
entityManager.flush();
log.info("Warehouse created successfully with ID: {}", savedWarehouse.getId());
return savedWarehouse;
}

public StorageWarehouse createWarehouse(StorageWarehouse warehouse) {
if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
throw new DuplicateResourceException("Warehouse with code " + warehouse.getWarehouseCode() + " already exists");
}
log.info("Creating new warehouse: {}", warehouse.getWarehouseName());
StorageWarehouse savedWarehouse = warehouseRepository.save(warehouse);
entityManager.flush();
log.info("Warehouse created successfully with ID: {}", savedWarehouse.getId());
return savedWarehouse;
}

@Transactional(readOnly = true)
public List<StorageWarehouse> getAllWarehouses() {
log.info("📊 Fetching all warehouses from database...");
List<StorageWarehouse> warehouses = warehouseRepository.findAll();
log.info("✅ Successfully fetched {} warehouses from database", warehouses.size());
if (warehouses.isEmpty()) {
log.warn("⚠️  WARNING: No warehouses found in database. Database may be empty or storage_warehouse table is not populated.");
} else {
log.info("📋 Warehouses found: {}", warehouses.stream()
.map(w -> String.format("%s (%s)", w.getWarehouseName(), w.getWarehouseCode()))
.collect(java.util.stream.Collectors.joining(", ")));
}
return warehouses;
}

@Transactional(readOnly = true)
public Page<StorageWarehouse> getWarehousesPaginated(Pageable pageable) {
log.info("📊 Fetching paginated warehouses from database (page: {}, size: {})...", pageable.getPageNumber(), pageable.getPageSize());
Page<StorageWarehouse> warehouses = warehouseRepository.findAll(pageable);
log.info("✅ Successfully fetched {} warehouses from database (page {} of {}, total: {})",
warehouses.getNumberOfElements(),
warehouses.getNumber() + 1,
warehouses.getTotalPages(),
warehouses.getTotalElements());
if (warehouses.isEmpty()) {
log.warn("⚠️  WARNING: No warehouses found in database for page {}. Database may be empty or storage_warehouse table is not populated.", pageable.getPageNumber());
}
return warehouses;
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
public List<StorageWarehouse> getWarehousesByType(WarehouseType type, Sort sort) {
return warehouseRepository.findByWarehouseType(type, sort);
}

@Transactional(readOnly = true)
public Page<StorageWarehouse> getWarehousesByType(WarehouseType type, Pageable pageable) {
return warehouseRepository.findByWarehouseType(type, pageable);
}

@Transactional(readOnly = true)
public List<StorageWarehouse> getWarehousesByStatus(WarehouseStatus status) {
return warehouseRepository.findByStatus(status);
}

@Transactional(readOnly = true)
public List<StorageWarehouse> getWarehousesByStatus(WarehouseStatus status, Sort sort) {
return warehouseRepository.findByStatus(status, sort);
}

@Transactional(readOnly = true)
public Page<StorageWarehouse> getWarehousesByStatus(WarehouseStatus status, Pageable pageable) {
return warehouseRepository.findByStatus(status, pageable);
}

@Transactional(readOnly = true)
public List<StorageWarehouse> getWarehousesByLocation(java.util.UUID locationId) {
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

StorageWarehouse savedWarehouse = warehouseRepository.save(warehouse);
entityManager.flush();
log.info("Warehouse updated successfully with ID: {}", savedWarehouse.getId());


try {
notificationService.notifyAllAdmins(
"Warehouse Updated",
String.format("Warehouse '%s' (%s) has been updated.",
savedWarehouse.getWarehouseName(),
savedWarehouse.getWarehouseCode()),
com.raf.enums.NotificationType.WAREHOUSE_UPDATED,
"/warehouses"
);
} catch (Exception e) {
log.error("Failed to send notification for warehouse update: {}", e.getMessage());
}

return savedWarehouse;
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
StorageWarehouse savedWarehouse = warehouseRepository.save(warehouse);
entityManager.flush();
return savedWarehouse;
}

public StorageWarehouse updateWarehouseStatus(Long id, WarehouseStatus status) {
StorageWarehouse warehouse = warehouseRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));
warehouse.setStatus(status);
log.info("Updating warehouse ID {} status to: {}", id, status);
StorageWarehouse savedWarehouse = warehouseRepository.save(warehouse);
entityManager.flush();
return savedWarehouse;
}

public void deleteWarehouse(Long id) {
StorageWarehouse warehouse = warehouseRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(WAREHOUSE_NOT_FOUND_WITH_ID + id));
String warehouseName = warehouse.getWarehouseName();
String warehouseCode = warehouse.getWarehouseCode();
log.info("Deleting warehouse ID: {}", id);
warehouseRepository.delete(warehouse);


try {
notificationService.notifyAllAdmins(
"Warehouse Deleted",
String.format("Warehouse '%s' (%s) has been deleted.", warehouseName, warehouseCode),
com.raf.enums.NotificationType.WAREHOUSE_DELETED,
"/warehouses"
);
} catch (Exception e) {
log.error("Failed to send notification for warehouse deletion: {}", e.getMessage());
}
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


@Transactional(readOnly = true)
public List<StorageWarehouse> getWarehousesByStorekeeperId(Long storekeeperId) {
log.info("Fetching warehouses for storekeeper ID: {}", storekeeperId);


List<com.raf.entity.WarehouseAccess> accesses = warehouseAccessRepository.findByUserId(storekeeperId);
log.debug("Found {} total warehouse accesses for storekeeper {}", accesses.size(), storekeeperId);


List<com.raf.entity.WarehouseAccess> managerAccesses = accesses.stream()
.filter(access -> access.getAccessLevel() == AccessLevel.MANAGER)
.filter(access -> Boolean.TRUE.equals(access.getIsActive()))
.toList();

log.info("Found {} MANAGER-level active warehouse accesses for storekeeper {}", managerAccesses.size(), storekeeperId);


List<Long> warehouseIds = managerAccesses.stream()
.map(access -> {
try {
return access.getWarehouse() != null ? access.getWarehouse().getId() : null;
} catch (Exception e) {
log.warn("Error getting warehouse ID from access: {}", e.getMessage());
return null;
}
})
.filter(id -> id != null)
.distinct()
.toList();

if (warehouseIds.isEmpty()) {
log.warn("No warehouse IDs found for storekeeper {}. Returning empty list.", storekeeperId);
return List.of();
}

log.info("Fetching {} warehouses for storekeeper {}: {}", warehouseIds.size(), storekeeperId, warehouseIds);


List<StorageWarehouse> warehouses = warehouseRepository.findAllById(warehouseIds);

log.info("Successfully fetched {} warehouses for storekeeper {}: {}",
warehouses.size(),
storekeeperId,
warehouses.stream()
.map(w -> String.format("%s (ID: %d)", w.getWarehouseName(), w.getId()))
.collect(java.util.stream.Collectors.joining(", ")));

return warehouses;
}


public StorageWarehouse createWarehouseWithCropTypes(WarehouseCreateRequest request) {
log.info("Creating warehouse with crop types: {}", request.getWarehouseName());

if (warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
throw new DuplicateResourceException("Warehouse with code " + request.getWarehouseCode() + " already exists");
}

Location location = locationRepository.findById(request.getLocationId())
.orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));


List<CropType> cropTypes = cropTypeRepository.findAllById(request.getCropTypeIds());
if (cropTypes.size() != request.getCropTypeIds().size()) {
throw new ResourceNotFoundException("One or more crop types not found");
}

StorageWarehouse warehouse = new StorageWarehouse();
warehouse.setWarehouseCode(request.getWarehouseCode());
warehouse.setWarehouseName(request.getWarehouseName());
warehouse.setWarehouseType(request.getWarehouseType());
warehouse.setTotalCapacityKg(request.getTotalCapacityKg());
warehouse.setAvailableCapacityKg(request.getTotalCapacityKg());
warehouse.setStatus(WarehouseStatus.ACTIVE);
warehouse.setLocation(location);
warehouse.setSupportedCropTypes(cropTypes);

StorageWarehouse savedWarehouse = warehouseRepository.save(warehouse);
entityManager.flush();
log.info("Created warehouse {} with ID {} and {} supported crop types", savedWarehouse.getWarehouseName(), savedWarehouse.getId(), cropTypes.size());

return savedWarehouse;
}
}
