package com.raf.service;

import com.raf.dto.InventoryRequest;
import com.raf.dto.InventoryUpdateRequest;
import com.raf.entity.CropType;
import com.raf.entity.Inventory;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.User;
import com.raf.enums.InventoryStatus;
import com.raf.enums.UserType;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.InventoryRepository;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
private final WarehouseAccessRepository warehouseAccessRepository;
@Lazy
private final NotificationService notificationService;

@PersistenceContext
private EntityManager entityManager;

public Inventory createInventoryFromRequest(InventoryRequest request) {
log.info("Creating inventory from request: {}", request.getInventoryCode());



String inventoryCode = generateUniqueInventoryCode();
log.info("Generated unique inventory code: {} (original from request: {})", inventoryCode, request.getInventoryCode());

User farmer = userRepository.findById(request.getFarmerId())
.orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + request.getFarmerId()));

StorageWarehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));

CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + request.getCropTypeId()));



if (request.getDesiredPricePerKg() != null && request.getDesiredPricePerKg().compareTo(BigDecimal.ZERO) > 0) {
cropType.setPricePerKg(request.getDesiredPricePerKg());
cropTypeRepository.save(cropType);
log.info("Updated crop type {} price to {} per {}", cropType.getCropName(), request.getDesiredPricePerKg(), cropType.getMeasurementUnit());
}

User storekeeper = userRepository.findById(request.getStorekeeperId())
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found with ID: " + request.getStorekeeperId()));

Inventory inventory = new Inventory();
inventory.setInventoryCode(inventoryCode);
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
inventory.setCropImageUrl(request.getCropImageUrl());


int maxRetries = 5;
int attempt = 0;
Inventory savedInventory = null;

while (attempt < maxRetries) {
try {

if (attempt > 0) {
inventoryCode = generateUniqueInventoryCode();
inventory.setInventoryCode(inventoryCode);
log.info("Retry attempt {}: Using new inventory code: {}", attempt, inventoryCode);
}

savedInventory = inventoryRepository.save(inventory);
entityManager.flush();


try {
BigDecimal newAvailableCapacity = warehouse.getAvailableCapacityKg().subtract(request.getQuantityKg());
if (newAvailableCapacity.compareTo(BigDecimal.ZERO) < 0) {
log.warn("Warehouse capacity would be negative, setting to 0. Warehouse: {}", warehouse.getId());
newAvailableCapacity = BigDecimal.ZERO;
}
warehouse.setAvailableCapacityKg(newAvailableCapacity);
warehouseRepository.save(warehouse);
entityManager.flush();
log.info("Updated warehouse {} available capacity to {} KG", warehouse.getId(), newAvailableCapacity);
} catch (Exception e) {
log.error("Failed to update warehouse capacity, but inventory was created: {}", e.getMessage());

}

log.info("Inventory created successfully with ID: {} and code: {}", savedInventory.getId(), savedInventory.getInventoryCode());


if (savedInventory.getFarmer() != null) savedInventory.getFarmer().getId();
if (savedInventory.getWarehouse() != null) savedInventory.getWarehouse().getId();
if (savedInventory.getCropType() != null) savedInventory.getCropType().getId();
if (savedInventory.getStorekeeper() != null) savedInventory.getStorekeeper().getId();

return savedInventory;

} catch (org.springframework.dao.DataIntegrityViolationException e) {
attempt++;
log.warn("Data integrity violation on attempt {}: {}", attempt, e.getMessage());

if (e.getMessage() != null && e.getMessage().contains("inventory_code")) {
if (attempt < maxRetries) {
log.info("Inventory code conflict detected, will retry with new code (attempt {}/{})", attempt, maxRetries);
continue;
} else {
log.error("Failed to create inventory after {} attempts due to code conflicts", maxRetries);
throw new DuplicateResourceException("Unable to generate unique inventory code after multiple attempts. Please try again.");
}
} else {

log.error("Data integrity violation (non-code related): {}", e.getMessage(), e);
String errorMessage = "Failed to create inventory due to data constraint violation.";
if (e.getMessage() != null) {
if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
errorMessage = "Invalid reference data. Please verify farmer, warehouse, crop type, and storekeeper are valid.";
}
}
throw new DuplicateResourceException(errorMessage);
}
} catch (Exception e) {
log.error("Unexpected error creating inventory: {}", e.getMessage(), e);
throw new RuntimeException("Failed to create inventory: " + e.getMessage(), e);
}
}


throw new RuntimeException("Failed to create inventory after all retry attempts");
}


private String generateUniqueInventoryCode() {

String uuid = java.util.UUID.randomUUID().toString().toUpperCase().replace("-", "");

return "INV-" + uuid;
}

public Inventory createInventory(Inventory inventory) {
log.info("Creating inventory: {} for farmer {}",
inventory.getInventoryCode(), inventory.getFarmer().getUserCode());

if (inventoryRepository.existsByInventoryCode(inventory.getInventoryCode())) {
throw new DuplicateResourceException("Inventory code already exists: " + inventory.getInventoryCode());
}

inventory.setRemainingQuantityKg(inventory.getQuantityKg());
inventory.setStatus(InventoryStatus.STORED);
inventory.setStorageDate(LocalDate.now());

Inventory savedInventory = inventoryRepository.save(inventory);
entityManager.flush();
log.info("Inventory created successfully with ID: {} and code: {}", savedInventory.getId(), savedInventory.getInventoryCode());
return savedInventory;
}

public Inventory getInventoryById(Long id) {
return inventoryRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
}

public Inventory getInventoryByCode(String code) {
return inventoryRepository.findByInventoryCode(code)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with code: " + code));
}

public List<Inventory> getAllInventories() {
log.info("📊 Fetching all inventories from database...");
List<Inventory> inventories = inventoryRepository.findAll();
log.info("📦 Found {} total inventories in database", inventories.size());

List<Inventory> validInventories = inventories.stream()
.filter(inv -> {
try {
if (inv.getFarmer() == null) return false;
Long farmerId = inv.getFarmer().getId();
if (farmerId == null) return false;
User farmer = userRepository.findById(farmerId).orElse(null);
if (farmer == null || farmer.getUserType() != com.raf.enums.UserType.FARMER) {
return false;
}

if (inv.getWarehouse() != null) inv.getWarehouse().getId();
if (inv.getCropType() != null) inv.getCropType().getId();
if (inv.getStorekeeper() != null) inv.getStorekeeper().getId();
return true;
} catch (Exception e) {
log.warn("Error validating inventory {}: {}", inv.getInventoryCode(), e.getMessage());
return false;
}
})
.collect(java.util.stream.Collectors.toList());
log.info("✅ Successfully fetched {} valid inventories from database", validInventories.size());
if (validInventories.isEmpty() && !inventories.isEmpty()) {
log.warn("⚠️  WARNING: {} inventories found but {} were filtered out due to invalid data (missing farmer, invalid farmer type, etc.)", inventories.size(), inventories.size() - validInventories.size());
} else if (validInventories.isEmpty()) {
log.warn("⚠️  WARNING: No inventories found in database. Database may be empty or inventory table is not populated.");
} else {
log.info("📋 Valid inventories: {}", validInventories.stream()
.map(inv -> String.format("%s (%s - %s kg)", inv.getInventoryCode(),
inv.getCropType() != null ? inv.getCropType().getCropName() : "Unknown",
inv.getQuantityKg()))
.limit(5)
.collect(java.util.stream.Collectors.joining(", ")) + (validInventories.size() > 5 ? "..." : ""));
}
return validInventories;
}

public List<Inventory> getInventoriesByFarmer(Long farmerId) {
return inventoryRepository.findByFarmerId(farmerId);
}

public List<Inventory> getInventoriesByWarehouse(Long warehouseId) {
List<Inventory> inventories = inventoryRepository.findByWarehouseId(warehouseId);
return filterValidInventories(inventories);
}

public List<Inventory> getInventoriesByWarehouseAndStorekeeper(Long warehouseId, Long storekeeperId) {
List<Inventory> inventories = inventoryRepository.findByWarehouseId(warehouseId);
List<Inventory> validInventories = filterValidInventories(inventories);

return validInventories.stream()
.filter(inv -> inv.getStorekeeper() != null && inv.getStorekeeper().getId().equals(storekeeperId))
.collect(java.util.stream.Collectors.toList());
}

public List<Inventory> getInventoriesByCropType(Long cropTypeId) {
List<Inventory> inventories = inventoryRepository.findByCropTypeId(cropTypeId);
return filterValidInventories(inventories);
}

public List<Inventory> getInventoriesByStatus(InventoryStatus status) {
List<Inventory> inventories = inventoryRepository.findByStatus(status);
return filterValidInventories(inventories);
}

public List<Inventory> getInventoriesByStatus(InventoryStatus status, Sort sort) {
List<Inventory> inventories = inventoryRepository.findByStatus(status, sort);
return filterValidInventories(inventories);
}

public Page<Inventory> getInventoriesByStatus(InventoryStatus status, Pageable pageable) {
Page<Inventory> inventories = inventoryRepository.findByStatus(status, pageable);

List<Inventory> validInventories = inventories.getContent().stream()
.filter(inv -> {
try {
if (inv.getFarmer() == null) return false;
Long farmerId = inv.getFarmer().getId();
if (farmerId == null) return false;
User farmer = userRepository.findById(farmerId).orElse(null);
if (farmer == null || farmer.getUserType() != com.raf.enums.UserType.FARMER) {
return false;
}

if (inv.getWarehouse() != null) inv.getWarehouse().getId();
if (inv.getCropType() != null) inv.getCropType().getId();
if (inv.getStorekeeper() != null) inv.getStorekeeper().getId();
return true;
} catch (Exception e) {
log.warn("Error validating inventory {}: {}", inv.getInventoryCode(), e.getMessage());
return false;
}
})
.collect(java.util.stream.Collectors.toList());

return new org.springframework.data.domain.PageImpl<>(validInventories, pageable, validInventories.size());
}

public List<Inventory> getInventoriesByFarmer(Long farmerId, Sort sort) {
return inventoryRepository.findByFarmerId(farmerId);
}

public Page<Inventory> getInventoriesByFarmer(Long farmerId, Pageable pageable) {
return inventoryRepository.findByFarmerId(farmerId, pageable);
}

public Page<Inventory> getInventoriesByWarehouse(Long warehouseId, Pageable pageable) {
Page<Inventory> inventories = inventoryRepository.findByWarehouseId(warehouseId, pageable);

List<Inventory> validInventories = inventories.getContent().stream()
.filter(inv -> {
try {
if (inv.getFarmer() == null) return false;
Long farmerId = inv.getFarmer().getId();
if (farmerId == null) return false;
User farmer = userRepository.findById(farmerId).orElse(null);
if (farmer == null || farmer.getUserType() != com.raf.enums.UserType.FARMER) {
return false;
}

if (inv.getWarehouse() != null) inv.getWarehouse().getId();
if (inv.getCropType() != null) inv.getCropType().getId();
if (inv.getStorekeeper() != null) inv.getStorekeeper().getId();
return true;
} catch (Exception e) {
log.warn("Error validating inventory {}: {}", inv.getInventoryCode(), e.getMessage());
return false;
}
})
.collect(java.util.stream.Collectors.toList());

return new org.springframework.data.domain.PageImpl<>(validInventories, pageable, validInventories.size());
}

public List<Inventory> getInventoriesByLocationCode(String locationCode) {
return inventoryRepository.findInventoriesByLocationCode(locationCode);
}


@Transactional(readOnly = true)
public List<Inventory> getAvailableInventoriesForBuyers() {
log.info("Fetching available inventories for buyers (STORED status with remaining quantity > 0)");
List<Inventory> storedInventories = inventoryRepository.findByStatus(InventoryStatus.STORED);
List<Inventory> validInventories = filterValidInventories(storedInventories);


List<Inventory> availableInventories = validInventories.stream()
.filter(inv -> inv.getRemainingQuantityKg() != null
&& inv.getRemainingQuantityKg().compareTo(BigDecimal.ZERO) > 0)
.collect(java.util.stream.Collectors.toList());

log.info("Found {} available inventories for buyers (out of {} stored inventories)",
availableInventories.size(), validInventories.size());

return availableInventories;
}

public Page<Inventory> getInventoriesPaginated(Pageable pageable) {
Page<Inventory> inventories = inventoryRepository.findAll(pageable);

inventories.getContent().forEach(inv -> {
if (inv.getFarmer() != null) inv.getFarmer().getId();
if (inv.getWarehouse() != null) inv.getWarehouse().getId();
if (inv.getCropType() != null) inv.getCropType().getId();
if (inv.getStorekeeper() != null) inv.getStorekeeper().getId();
});
return inventories;
}

public Inventory updateInventory(Long id, Inventory inventoryDetails) {
log.info("Updating inventory with id: {}", id);

Inventory inventory = inventoryRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

inventory.setQualityGrade(inventoryDetails.getQualityGrade());
inventory.setExpectedWithdrawalDate(inventoryDetails.getExpectedWithdrawalDate());
inventory.setNotes(inventoryDetails.getNotes());

Inventory savedInventory = inventoryRepository.save(inventory);
entityManager.flush();


try {
notificationService.notifyAllAdmins(
"Inventory Updated",
String.format("Inventory '%s' has been updated.", savedInventory.getInventoryCode()),
com.raf.enums.NotificationType.INVENTORY_UPDATED,
"/inventory"
);
} catch (Exception e) {
log.error("Failed to send notification for inventory update: {}", e.getMessage());
}

return savedInventory;
}


public Inventory updateInventoryByStorekeeper(Long inventoryId, InventoryUpdateRequest request, Long storekeeperId) {
log.info("Storekeeper {} updating inventory {}", storekeeperId, inventoryId);

Inventory inventory = inventoryRepository.findById(inventoryId)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + inventoryId));


if (inventory.getStorekeeper() == null) {
throw new IllegalArgumentException("Inventory does not have an assigned storekeeper");
}
if (!inventory.getStorekeeper().getId().equals(storekeeperId)) {
throw new IllegalArgumentException("Unauthorized: You can only update inventory assigned to you");
}


User storekeeper = userRepository.findById(storekeeperId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found"));
if (storekeeper.getUserType() != UserType.STOREKEEPER) {
throw new IllegalArgumentException("Only storekeepers can update inventory using this method");
}


if (request.getCropImageUrl() != null && !request.getCropImageUrl().trim().isEmpty()) {
inventory.setCropImageUrl(request.getCropImageUrl().trim());
log.info("Updated crop image URL for inventory {}", inventoryId);
}


if (request.getQuantityKg() != null && request.getQuantityKg().compareTo(BigDecimal.ZERO) > 0) {
BigDecimal oldQuantity = inventory.getQuantityKg();
inventory.setQuantityKg(request.getQuantityKg());


if (request.getRemainingQuantityKg() != null) {
inventory.setRemainingQuantityKg(request.getRemainingQuantityKg());
} else {

BigDecimal ratio = request.getQuantityKg().divide(oldQuantity, 4, java.math.RoundingMode.HALF_UP);
BigDecimal newRemaining = inventory.getRemainingQuantityKg().multiply(ratio);
inventory.setRemainingQuantityKg(newRemaining);
}
log.info("Updated quantity for inventory {} from {} to {}", inventoryId, oldQuantity, request.getQuantityKg());
}


if (request.getDesiredPricePerKg() != null && request.getDesiredPricePerKg().compareTo(BigDecimal.ZERO) > 0) {
CropType cropType = inventory.getCropType();
if (cropType != null) {
cropType.setPricePerKg(request.getDesiredPricePerKg());
cropTypeRepository.save(cropType);
log.info("Updated desired price for crop type {} to {} per {}",
cropType.getCropName(), request.getDesiredPricePerKg(), cropType.getMeasurementUnit());
}
}

Inventory savedInventory = inventoryRepository.save(inventory);
entityManager.flush();


try {
notificationService.createNotification(
inventory.getFarmer().getId(),
"Inventory Updated",
String.format("Storekeeper has updated your inventory '%s' in warehouse '%s'.",
savedInventory.getInventoryCode(),
savedInventory.getWarehouse().getWarehouseName()),
com.raf.enums.NotificationType.INVENTORY_UPDATED,
null,
"/inventory"
);
} catch (Exception e) {
log.error("Failed to send notification to farmer: {}", e.getMessage());
}

log.info("✅ Successfully updated inventory {} by storekeeper {}", inventoryId, storekeeperId);
return savedInventory;
}

public Inventory reduceInventoryQuantity(Long id, BigDecimal soldQuantity) {
log.info("Reducing inventory {} by {} kg", id, soldQuantity);

Inventory inventory = inventoryRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

if (inventory.getRemainingQuantityKg().compareTo(soldQuantity) < 0) {
throw new IllegalArgumentException("Insufficient quantity available. Available: " +
inventory.getRemainingQuantityKg() + " kg");
}

BigDecimal newRemaining = inventory.getRemainingQuantityKg().subtract(soldQuantity);
inventory.setRemainingQuantityKg(newRemaining);

if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
inventory.setStatus(InventoryStatus.SOLD);
} else {
inventory.setStatus(InventoryStatus.PARTIALLY_SOLD);
}

Inventory savedInventory = inventoryRepository.save(inventory);
entityManager.flush();
return savedInventory;
}

public Inventory withdrawInventory(Long id) {
log.info("Marking inventory {} as withdrawn", id);

Inventory inventory = inventoryRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
inventory.setStatus(InventoryStatus.WITHDRAWN);
inventory.setRemainingQuantityKg(BigDecimal.ZERO);

Inventory savedInventory = inventoryRepository.save(inventory);
entityManager.flush();
return savedInventory;
}

public void deleteInventory(Long id) {
log.info("Deleting inventory with id: {}", id);
Inventory inventory = inventoryRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
String inventoryCode = inventory.getInventoryCode();
inventoryRepository.delete(inventory);


try {
notificationService.notifyAllAdmins(
"Inventory Deleted",
String.format("Inventory '%s' has been deleted.", inventoryCode),
com.raf.enums.NotificationType.INVENTORY_DELETED,
"/inventory"
);
} catch (Exception e) {
log.error("Failed to send notification for inventory deletion: {}", e.getMessage());
}
}


public Inventory createInventoryByStorekeeper(InventoryRequest request, Long storekeeperId) {
log.info("Storekeeper {} creating inventory in warehouse {}", storekeeperId, request.getWarehouseId());


User storekeeper = userRepository.findById(storekeeperId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found with ID: " + storekeeperId));
if (storekeeper.getUserType() != UserType.STOREKEEPER) {
throw new IllegalArgumentException("Only storekeepers can create inventory using this method");
}


StorageWarehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));


boolean hasAccess = warehouseAccessRepository.existsByUserIdAndWarehouseId(storekeeperId, request.getWarehouseId());
if (hasAccess) {

var access = warehouseAccessRepository.findByUserIdAndWarehouseId(storekeeperId, request.getWarehouseId());
if (access.isEmpty() || !access.get().getIsActive()) {
hasAccess = false;
}
}

if (!hasAccess) {
throw new IllegalArgumentException("Unauthorized: You can only create inventory in warehouses assigned to you");
}


Inventory inventory = createInventoryFromRequest(request);


try {
notificationService.createNotification(
request.getFarmerId(),
"Inventory Created",
String.format("Storekeeper has created inventory '%s' for your crop in warehouse '%s'.",
inventory.getInventoryCode(),
warehouse.getWarehouseName()),
com.raf.enums.NotificationType.INVENTORY_UPDATED,
null,
"/inventory"
);
} catch (Exception e) {
log.error("Failed to send notification to farmer: {}", e.getMessage());
}

log.info("✅ Successfully created inventory {} by storekeeper {}", inventory.getInventoryCode(), storekeeperId);
return inventory;
}


public void deleteInventoryByStorekeeper(Long inventoryId, Long storekeeperId) {
log.info("Storekeeper {} deleting inventory {}", storekeeperId, inventoryId);

Inventory inventory = inventoryRepository.findById(inventoryId)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + inventoryId));


if (!inventory.getStorekeeper().getId().equals(storekeeperId)) {
throw new IllegalArgumentException("Unauthorized: You can only delete inventory assigned to you");
}


User storekeeper = userRepository.findById(storekeeperId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found"));
if (storekeeper.getUserType() != UserType.STOREKEEPER) {
throw new IllegalArgumentException("Only storekeepers can delete inventory using this method");
}


try {
StorageWarehouse warehouse = inventory.getWarehouse();
BigDecimal returnedCapacity = warehouse.getAvailableCapacityKg().add(inventory.getQuantityKg());
warehouse.setAvailableCapacityKg(returnedCapacity);
warehouseRepository.save(warehouse);
entityManager.flush();
log.info("Returned {} KG capacity to warehouse {}", inventory.getQuantityKg(), warehouse.getId());
} catch (Exception e) {
log.error("Failed to return capacity to warehouse: {}", e.getMessage());
}


try {
notificationService.createNotification(
inventory.getFarmer().getId(),
"Inventory Deleted",
String.format("Storekeeper has deleted inventory '%s' from warehouse '%s'.",
inventory.getInventoryCode(),
inventory.getWarehouse().getWarehouseName()),
com.raf.enums.NotificationType.INVENTORY_UPDATED,
null,
"/inventory"
);
} catch (Exception e) {
log.error("Failed to send notification to farmer: {}", e.getMessage());
}

String inventoryCode = inventory.getInventoryCode();
inventoryRepository.delete(inventory);
entityManager.flush();

log.info("✅ Successfully deleted inventory {} by storekeeper {}", inventoryCode, storekeeperId);
}


public int cleanupInventoryWithInvalidFarmers() {
log.info("Starting cleanup of inventory items with invalid farmers...");

List<Inventory> allInventories = inventoryRepository.findAll();
int deletedCount = 0;
List<String> deletedCodes = new java.util.ArrayList<>();

for (Inventory inventory : allInventories) {
try {
boolean shouldDelete = false;
String reason = "";


if (inventory.getFarmer() == null) {
shouldDelete = true;
reason = "Farmer is null";
} else {

Long farmerId = inventory.getFarmer().getId();
if (farmerId == null) {
shouldDelete = true;
reason = "Farmer ID is null";
} else {

User farmer = userRepository.findById(farmerId).orElse(null);
if (farmer == null) {
shouldDelete = true;
reason = "Farmer does not exist in database";
} else if (farmer.getUserType() != com.raf.enums.UserType.FARMER) {
shouldDelete = true;
reason = "User is not a FARMER (type: " + farmer.getUserType() + ")";
}
}
}

if (shouldDelete) {
String inventoryCode = inventory.getInventoryCode();
log.info("Deleting inventory {} - Reason: {}", inventoryCode, reason);


try {
StorageWarehouse warehouse = inventory.getWarehouse();
if (warehouse != null) {
BigDecimal quantity = inventory.getQuantityKg();
if (quantity != null) {
BigDecimal currentCapacity = warehouse.getAvailableCapacityKg();
warehouse.setAvailableCapacityKg(currentCapacity.add(quantity));
warehouseRepository.save(warehouse);
log.debug("Restored {} KG capacity to warehouse {}", quantity, warehouse.getWarehouseName());
}
}
} catch (Exception e) {
log.warn("Failed to restore warehouse capacity for inventory {}: {}",
inventory.getInventoryCode(), e.getMessage());
}

inventoryRepository.delete(inventory);
deletedCount++;
deletedCodes.add(inventoryCode);
}
} catch (Exception e) {
log.error("Error processing inventory {}: {}", inventory.getInventoryCode(), e.getMessage(), e);
}
}

entityManager.flush();

log.info("Cleanup completed. Deleted {} inventory items with invalid farmers.", deletedCount);
if (!deletedCodes.isEmpty()) {
log.info("Deleted inventory codes: {}", String.join(", ", deletedCodes));
}

return deletedCount;
}


private List<Inventory> filterValidInventories(List<Inventory> inventories) {
return inventories.stream()
.filter(inv -> {
try {
if (inv.getFarmer() == null) return false;
Long farmerId = inv.getFarmer().getId();
if (farmerId == null) return false;
User farmer = userRepository.findById(farmerId).orElse(null);
if (farmer == null || farmer.getUserType() != com.raf.enums.UserType.FARMER) {
return false;
}

if (inv.getWarehouse() != null) inv.getWarehouse().getId();
if (inv.getCropType() != null) inv.getCropType().getId();
if (inv.getStorekeeper() != null) inv.getStorekeeper().getId();
return true;
} catch (Exception e) {
log.warn("Error validating inventory {}: {}", inv.getInventoryCode(), e.getMessage());
return false;
}
})
.collect(java.util.stream.Collectors.toList());
}

public BigDecimal getTotalStoredQuantityInWarehouse(Long warehouseId) {
BigDecimal total = inventoryRepository.getTotalStoredQuantityInWarehouse(warehouseId);
return total != null ? total : BigDecimal.ZERO;
}

public long countActiveInventoriesByFarmer(Long farmerId) {
return inventoryRepository.countActiveInventoriesByFarmer(farmerId);
}

public List<Inventory> getAvailableInventoryInWarehouse(Long warehouseId, BigDecimal minQuantity) {
List<Inventory> inventories = inventoryRepository.findAvailableInventoryInWarehouse(
warehouseId, InventoryStatus.STORED, minQuantity);
return filterValidInventories(inventories);
}
}

