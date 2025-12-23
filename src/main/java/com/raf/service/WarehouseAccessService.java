package com.raf.service;

import com.raf.dto.WarehouseAccessRequest;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.User;
import com.raf.entity.WarehouseAccess;
import com.raf.enums.AccessLevel;
import com.raf.enums.UserType;
import com.raf.enums.WarehouseAccessStatus;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.OperationNotAllowedException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.exception.UnauthorizedException;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import com.raf.repository.InventoryRepository;
import com.raf.entity.Inventory;
import com.raf.enums.InventoryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseAccessService {
private final WarehouseAccessRepository warehouseAccessRepository;
private final UserRepository userRepository;
private final StorageWarehouseRepository warehouseRepository;
private final com.raf.repository.CropTypeRepository cropTypeRepository;
private final InventoryRepository inventoryRepository;
private final NotificationService notificationService;

@PersistenceContext
private EntityManager entityManager;

public WarehouseAccess createWarehouseAccessFromRequest(WarehouseAccessRequest request) {
log.info("Creating warehouse access from request for user ID: {} and warehouse ID: {}",
request.getUserId(), request.getWarehouseId());

User user = userRepository.findById(request.getUserId())
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

StorageWarehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));




Optional<WarehouseAccess> existingAccess = warehouseAccessRepository.findByUserIdAndWarehouseId(
request.getUserId(), request.getWarehouseId());

if (existingAccess.isPresent()) {
WarehouseAccess existing = existingAccess.get();


if (existing.getStatus() == com.raf.enums.WarehouseAccessStatus.PENDING) {
log.info("Updating existing pending warehouse access request ID: {}", existing.getId());

existing.setRequestedCapacityKg(request.getRequestedCapacityKg());
existing.setNotes(request.getNotes());


if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new com.raf.exception.ResourceNotFoundException("Crop type not found with ID: " + request.getCropTypeId()));
existing.setCropType(cropType);
}
existing.setCropQuantityKg(request.getCropQuantityKg());
existing.setQualityGrade(request.getQualityGrade());
existing.setExpectedStorageDate(request.getExpectedStorageDate());
existing.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
existing.setCropNotes(request.getCropNotes());
existing.setDesiredPricePerKg(request.getDesiredPricePerKg());

WarehouseAccess updatedAccess = warehouseAccessRepository.save(existing);


try {
notificationService.createNotification(
user.getId(),
"Warehouse Access Request Updated",
String.format("Your warehouse access request for %s has been updated. Status: PENDING",
warehouse.getWarehouseName()),
com.raf.enums.NotificationType.WAREHOUSE_ACCESS_SUBMITTED,
null,
"/farmer/dashboard"
);
notifyStorekeepers(warehouse.getId(), user, request);
} catch (Exception e) {
log.error("Failed to create notifications for warehouse access update, but request was saved: {}", e.getMessage());

}

return updatedAccess;
}



if (existing.getStatus() == com.raf.enums.WarehouseAccessStatus.APPROVED ||
existing.getStatus() == com.raf.enums.WarehouseAccessStatus.ACTIVE) {
log.info("Farmer already has {} access to warehouse {}. Allowing new inventory request by resetting status to PENDING.",
existing.getStatus(), warehouse.getWarehouseName());

existing.setStatus(com.raf.enums.WarehouseAccessStatus.PENDING);
existing.setRequestedCapacityKg(request.getRequestedCapacityKg());
existing.setNotes(request.getNotes());


if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new com.raf.exception.ResourceNotFoundException("Crop type not found with ID: " + request.getCropTypeId()));
existing.setCropType(cropType);
}
existing.setCropQuantityKg(request.getCropQuantityKg());
existing.setQualityGrade(request.getQualityGrade());
existing.setExpectedStorageDate(request.getExpectedStorageDate());
existing.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
existing.setCropNotes(request.getCropNotes());
existing.setDesiredPricePerKg(request.getDesiredPricePerKg());

WarehouseAccess updatedAccess = warehouseAccessRepository.save(existing);


try {
notificationService.createNotification(
user.getId(),
"New Inventory Request Submitted",
String.format("Your new inventory request for %s has been submitted. Status: PENDING",
warehouse.getWarehouseName()),
com.raf.enums.NotificationType.WAREHOUSE_ACCESS_SUBMITTED,
null,
"/farmer/dashboard"
);
notifyStorekeepers(warehouse.getId(), user, request);
} catch (Exception e) {
log.error("Failed to create notifications for new inventory request, but request was saved: {}", e.getMessage());
}

return updatedAccess;
}


if (existing.getStatus() == com.raf.enums.WarehouseAccessStatus.REJECTED) {
log.info("Deleting rejected warehouse access request to allow new submission");
warehouseAccessRepository.delete(existing);

}
}


WarehouseAccess warehouseAccess = new WarehouseAccess();
warehouseAccess.setUser(user);
warehouseAccess.setWarehouse(warehouse);
warehouseAccess.setAccessLevel(request.getAccessLevel());
warehouseAccess.setGrantedDate(request.getGrantedDate());
warehouseAccess.setExpiryDate(request.getExpiryDate());
warehouseAccess.setIsActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE);
warehouseAccess.setStatus(request.getStatus() != null ? request.getStatus() : com.raf.enums.WarehouseAccessStatus.PENDING);
warehouseAccess.setRequestedCapacityKg(request.getRequestedCapacityKg());
warehouseAccess.setNotes(request.getNotes());


if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new com.raf.exception.ResourceNotFoundException("Crop type not found with ID: " + request.getCropTypeId()));
warehouseAccess.setCropType(cropType);
}
warehouseAccess.setCropQuantityKg(request.getCropQuantityKg());
warehouseAccess.setQualityGrade(request.getQualityGrade());
warehouseAccess.setExpectedStorageDate(request.getExpectedStorageDate());
warehouseAccess.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
warehouseAccess.setCropNotes(request.getCropNotes());
warehouseAccess.setCropImageUrl(request.getCropImageUrl());
warehouseAccess.setDesiredPricePerKg(request.getDesiredPricePerKg());


Optional<WarehouseAccess> finalCheck = warehouseAccessRepository.findByUserIdAndWarehouseId(
request.getUserId(), request.getWarehouseId());
if (finalCheck.isPresent()) {
WarehouseAccess existing = finalCheck.get();

if (existing.getStatus() == com.raf.enums.WarehouseAccessStatus.PENDING) {
log.info("Found existing pending request during final check, updating it");
existing.setRequestedCapacityKg(request.getRequestedCapacityKg());
existing.setNotes(request.getNotes());
if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new com.raf.exception.ResourceNotFoundException("Crop type not found"));
existing.setCropType(cropType);
}
existing.setCropQuantityKg(request.getCropQuantityKg());
existing.setQualityGrade(request.getQualityGrade());
existing.setExpectedStorageDate(request.getExpectedStorageDate());
existing.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
existing.setCropNotes(request.getCropNotes());
existing.setDesiredPricePerKg(request.getDesiredPricePerKg());

WarehouseAccess updated = warehouseAccessRepository.save(existing);


try {
notifyStorekeepers(warehouse.getId(), user, request);
} catch (Exception e) {
log.error("Failed to notify storekeepers for warehouse access update, but request was saved: {}", e.getMessage());

}

return updated;
}

if (existing.getStatus() == com.raf.enums.WarehouseAccessStatus.APPROVED ||
existing.getStatus() == com.raf.enums.WarehouseAccessStatus.ACTIVE) {
log.info("Farmer already has {} access. Allowing new inventory request by resetting to PENDING.",
existing.getStatus());
existing.setStatus(com.raf.enums.WarehouseAccessStatus.PENDING);
existing.setRequestedCapacityKg(request.getRequestedCapacityKg());
existing.setNotes(request.getNotes());
if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new com.raf.exception.ResourceNotFoundException("Crop type not found"));
existing.setCropType(cropType);
}
existing.setCropQuantityKg(request.getCropQuantityKg());
existing.setQualityGrade(request.getQualityGrade());
existing.setExpectedStorageDate(request.getExpectedStorageDate());
existing.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
existing.setCropNotes(request.getCropNotes());
existing.setDesiredPricePerKg(request.getDesiredPricePerKg());

WarehouseAccess updated = warehouseAccessRepository.save(existing);

try {
notifyStorekeepers(warehouse.getId(), user, request);
} catch (Exception e) {
log.error("Failed to notify storekeepers, but request was saved: {}", e.getMessage());
}

return updated;
}
}

WarehouseAccess savedAccess;
try {
savedAccess = warehouseAccessRepository.save(warehouseAccess);
} catch (DataIntegrityViolationException e) {

Optional<WarehouseAccess> checkAgain = warehouseAccessRepository.findByUserIdAndWarehouseId(
request.getUserId(), request.getWarehouseId());
if (checkAgain.isPresent()) {
WarehouseAccess existing = checkAgain.get();

log.info("Found existing access during constraint violation. Resetting to PENDING for new inventory request.");
existing.setStatus(com.raf.enums.WarehouseAccessStatus.PENDING);
existing.setRequestedCapacityKg(request.getRequestedCapacityKg());
existing.setNotes(request.getNotes());
if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new com.raf.exception.ResourceNotFoundException("Crop type not found"));
existing.setCropType(cropType);
}
existing.setCropQuantityKg(request.getCropQuantityKg());
existing.setQualityGrade(request.getQualityGrade());
existing.setExpectedStorageDate(request.getExpectedStorageDate());
existing.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
existing.setCropNotes(request.getCropNotes());
existing.setDesiredPricePerKg(request.getDesiredPricePerKg());

WarehouseAccess updated = warehouseAccessRepository.save(existing);

try {
notifyStorekeepers(warehouse.getId(), user, request);
} catch (Exception notifyEx) {
log.error("Failed to notify storekeepers, but request was saved: {}", notifyEx.getMessage());
}

return updated;
}

throw new DuplicateResourceException(
"A warehouse access request already exists for this warehouse. Please check your existing requests.");
}



try {
notificationService.createNotification(
user.getId(),
"Warehouse Access Request Submitted",
String.format("Your warehouse access request for %s has been submitted successfully. Status: PENDING",
warehouse.getWarehouseName()),
com.raf.enums.NotificationType.WAREHOUSE_ACCESS_SUBMITTED,
null,
"/farmer/dashboard"
);
notifyStorekeepers(warehouse.getId(), user, request);
log.info("Created notifications for warehouse access submission: {}", savedAccess.getId());
} catch (Exception e) {
log.error("Failed to create notifications for warehouse access, but request was saved: {}", e.getMessage());

}

return savedAccess;
}

public WarehouseAccess createWarehouseAccess(WarehouseAccess warehouseAccess) {
log.info("Creating new warehouse access for user ID: {} and warehouse ID: {}",
warehouseAccess.getUser().getId(), warehouseAccess.getWarehouse().getId());
return warehouseAccessRepository.save(warehouseAccess);
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getAllWarehouseAccesses() {

return warehouseAccessRepository.findAllWithUserAndWarehouse();
}

@Transactional(readOnly = true)
public Page<WarehouseAccess> getWarehouseAccessesPaginated(Pageable pageable) {
return warehouseAccessRepository.findAll(pageable);
}

@Transactional(readOnly = true)
public WarehouseAccess getWarehouseAccessById(Long id) {
return warehouseAccessRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getWarehouseAccessesByUser(Long userId) {
return warehouseAccessRepository.findByUserId(userId);
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getWarehouseAccessesByWarehouse(Long warehouseId) {
return warehouseAccessRepository.findByWarehouseId(warehouseId);
}

@Transactional(readOnly = true)
public WarehouseAccess getWarehouseAccessByUserAndWarehouse(Long userId, Long warehouseId) {
return warehouseAccessRepository.findByUserIdAndWarehouseId(userId, warehouseId)
.orElseThrow(() -> new ResourceNotFoundException(
"Warehouse access not found for user ID: " + userId + " and warehouse ID: " + warehouseId));
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getWarehouseAccessesByAccessLevel(AccessLevel accessLevel) {
return warehouseAccessRepository.findByAccessLevel(accessLevel);
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getActiveWarehouseAccesses() {
LocalDate today = LocalDate.now();
return warehouseAccessRepository.findAll().stream()
.filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
.toList();
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getActiveWarehouseAccessesForUser(Long userId) {
LocalDate today = LocalDate.now();
return warehouseAccessRepository.findByUserId(userId).stream()
.filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
.toList();
}

public WarehouseAccess updateWarehouseAccess(Long id, WarehouseAccess warehouseAccessDetails) {
WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));


if (warehouseAccess.getStatus() != com.raf.enums.WarehouseAccessStatus.PENDING) {
throw new OperationNotAllowedException("Cannot update warehouse access request. Only PENDING requests can be edited.");
}

warehouseAccess.setAccessLevel(warehouseAccessDetails.getAccessLevel());
warehouseAccess.setGrantedDate(warehouseAccessDetails.getGrantedDate());
warehouseAccess.setExpiryDate(warehouseAccessDetails.getExpiryDate());
if (warehouseAccessDetails.getRequestedCapacityKg() != null) {
warehouseAccess.setRequestedCapacityKg(warehouseAccessDetails.getRequestedCapacityKg());
}
if (warehouseAccessDetails.getNotes() != null) {
warehouseAccess.setNotes(warehouseAccessDetails.getNotes());
}


if (warehouseAccessDetails.getCropType() != null) {
warehouseAccess.setCropType(warehouseAccessDetails.getCropType());
}
if (warehouseAccessDetails.getCropQuantityKg() != null) {
warehouseAccess.setCropQuantityKg(warehouseAccessDetails.getCropQuantityKg());
}
if (warehouseAccessDetails.getQualityGrade() != null) {
warehouseAccess.setQualityGrade(warehouseAccessDetails.getQualityGrade());
}
if (warehouseAccessDetails.getExpectedStorageDate() != null) {
warehouseAccess.setExpectedStorageDate(warehouseAccessDetails.getExpectedStorageDate());
}
if (warehouseAccessDetails.getExpectedWithdrawalDate() != null) {
warehouseAccess.setExpectedWithdrawalDate(warehouseAccessDetails.getExpectedWithdrawalDate());
}
if (warehouseAccessDetails.getCropNotes() != null) {
warehouseAccess.setCropNotes(warehouseAccessDetails.getCropNotes());
}
if (warehouseAccessDetails.getCropImageUrl() != null) {
warehouseAccess.setCropImageUrl(warehouseAccessDetails.getCropImageUrl());
}

log.info("Updating warehouse access ID: {}", id);
WarehouseAccess savedAccess = warehouseAccessRepository.save(warehouseAccess);
entityManager.flush();
return savedAccess;
}

public WarehouseAccess updateWarehouseAccessFromRequest(Long id, WarehouseAccessRequest request) {
WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));


if (warehouseAccess.getStatus() != com.raf.enums.WarehouseAccessStatus.PENDING) {
throw new OperationNotAllowedException("Cannot update warehouse access request. Only PENDING requests can be edited.");
}


if (request.getRequestedCapacityKg() != null) {
warehouseAccess.setRequestedCapacityKg(request.getRequestedCapacityKg());
}
if (request.getNotes() != null) {
warehouseAccess.setNotes(request.getNotes());
}


if (request.getCropTypeId() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + request.getCropTypeId()));
warehouseAccess.setCropType(cropType);
}
if (request.getCropQuantityKg() != null) {
warehouseAccess.setCropQuantityKg(request.getCropQuantityKg());
}
if (request.getQualityGrade() != null) {
warehouseAccess.setQualityGrade(request.getQualityGrade());
}
if (request.getExpectedStorageDate() != null) {
warehouseAccess.setExpectedStorageDate(request.getExpectedStorageDate());
}
if (request.getExpectedWithdrawalDate() != null) {
warehouseAccess.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
}
if (request.getCropNotes() != null) {
warehouseAccess.setCropNotes(request.getCropNotes());
}
if (request.getCropImageUrl() != null) {
warehouseAccess.setCropImageUrl(request.getCropImageUrl());
}
if (request.getDesiredPricePerKg() != null) {
warehouseAccess.setDesiredPricePerKg(request.getDesiredPricePerKg());
}

log.info("Updating warehouse access ID: {} from request", id);
WarehouseAccess savedAccess = warehouseAccessRepository.save(warehouseAccess);
entityManager.flush();
return savedAccess;
}

public WarehouseAccess updateAccessLevel(Long id, AccessLevel accessLevel) {
WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
warehouseAccess.setAccessLevel(accessLevel);

log.info("Updating warehouse access ID {} access level to: {}", id, accessLevel);
return warehouseAccessRepository.save(warehouseAccess);
}

public WarehouseAccess revokeWarehouseAccess(Long id) {
WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
warehouseAccess.setExpiryDate(LocalDate.now());

log.info("Revoking warehouse access ID: {}", id);
return warehouseAccessRepository.save(warehouseAccess);
}

public void deleteWarehouseAccess(Long id) {
WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
log.info("Deleting warehouse access ID: {}", id);
warehouseAccessRepository.delete(warehouseAccess);
}

@Transactional(readOnly = true)
public boolean hasAccess(Long userId, Long warehouseId) {
return warehouseAccessRepository.existsByUserIdAndWarehouseId(userId, warehouseId);
}

@Transactional(readOnly = true)
public long getTotalWarehouseAccesses() {
return warehouseAccessRepository.count();
}

@Transactional(readOnly = true)
public long countActiveAccessesForWarehouse(Long warehouseId) {
LocalDate today = LocalDate.now();
return warehouseAccessRepository.findByWarehouseId(warehouseId).stream()
.filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
.count();
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getWarehouseAccessesByStatus(com.raf.enums.WarehouseAccessStatus status) {
return warehouseAccessRepository.findByStatus(status);
}

@Transactional(readOnly = true)
public List<WarehouseAccess> getWarehouseAccessesByWarehouseAndStatus(Long warehouseId, com.raf.enums.WarehouseAccessStatus status) {
return warehouseAccessRepository.findByWarehouseIdAndStatus(warehouseId, status);
}

public WarehouseAccess updateStatus(Long id, com.raf.enums.WarehouseAccessStatus status, Long currentUserId) {

WarehouseAccess warehouseAccess = warehouseAccessRepository.findByIdWithUserAndWarehouse(id)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));


if (warehouseAccess.getWarehouse() == null) {
log.error("Warehouse access {} has null warehouse", id);
throw new ResourceNotFoundException("Warehouse information not found for access ID: " + id);
}
if (warehouseAccess.getUser() == null) {
log.error("Warehouse access {} has null user", id);
throw new ResourceNotFoundException("User information not found for access ID: " + id);
}


if (currentUserId != null) {
User currentUser = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));


if (currentUser.getUserType() == UserType.STOREKEEPER) {
Long warehouseId = warehouseAccess.getWarehouse().getId();
if (warehouseId == null) {
log.error("Warehouse ID is null for warehouse access {}", id);
throw new ResourceNotFoundException("Warehouse ID not found for access ID: " + id);
}
log.info("=== AUTHORIZATION CHECK START ===");
log.info("Storekeeper ID: {}", currentUserId);
log.info("Storekeeper Email: {}", currentUser.getEmail());
log.info("Warehouse ID: {}", warehouseId);
log.info("Warehouse Name: {}", warehouseAccess.getWarehouse().getWarehouseName());
log.info("Warehouse Access ID being approved: {}", id);



Optional<WarehouseAccess> storekeeperAccessOpt = Optional.empty();


try {
storekeeperAccessOpt = warehouseAccessRepository.findByUserIdAndWarehouseIdWithUserAndWarehouse(currentUserId, warehouseId);
log.debug("Method 1 (findByUserIdAndWarehouseIdWithUserAndWarehouse): Found = {}", storekeeperAccessOpt.isPresent());
} catch (Exception e) {
log.warn("Error in findByUserIdAndWarehouseIdWithUserAndWarehouse: {}", e.getMessage());
}


if (!storekeeperAccessOpt.isPresent()) {
try {
storekeeperAccessOpt = warehouseAccessRepository.findByUserIdAndWarehouseId(currentUserId, warehouseId);
log.debug("Method 2 (findByUserIdAndWarehouseId): Found = {}", storekeeperAccessOpt.isPresent());
} catch (Exception e) {
log.warn("Error in findByUserIdAndWarehouseId: {}", e.getMessage());
}
}


if (!storekeeperAccessOpt.isPresent()) {
try {
List<WarehouseAccess> allAccesses = warehouseAccessRepository.findByUserId(currentUserId);
log.debug("Method 2: Found {} total accesses for storekeeper {}", allAccesses.size(), currentUserId);

storekeeperAccessOpt = allAccesses.stream()
.filter(wa -> {
try {
Long waWarehouseId = wa.getWarehouse() != null ? wa.getWarehouse().getId() : null;
boolean matches = waWarehouseId != null && waWarehouseId.equals(warehouseId);
if (matches) {
log.debug("Found matching access: warehouseId={}, level={}, active={}",
waWarehouseId, wa.getAccessLevel(), wa.getIsActive());
}
return matches;
} catch (Exception e) {
log.warn("Error checking warehouse access: {}", e.getMessage());
return false;
}
})
.findFirst();
log.debug("Method 2 result: Found = {}", storekeeperAccessOpt.isPresent());
} catch (Exception e) {
log.error("Error in Method 2: {}", e.getMessage(), e);
}
}


if (!storekeeperAccessOpt.isPresent()) {
try {
boolean exists = warehouseAccessRepository.existsByUserIdAndWarehouseId(currentUserId, warehouseId);
log.debug("Method 3 (existsByUserIdAndWarehouseId): Exists = {}", exists);
if (exists) {
storekeeperAccessOpt = warehouseAccessRepository.findByUserIdAndWarehouseId(currentUserId, warehouseId);
log.debug("Method 3: Retrieved after exists check, Found = {}", storekeeperAccessOpt.isPresent());
}
} catch (Exception e) {
log.warn("Error in Method 3: {}", e.getMessage());
}
}

if (storekeeperAccessOpt.isPresent()) {
WarehouseAccess access = storekeeperAccessOpt.get();
log.info("Found warehouse access for storekeeper {}: accessLevel={}, isActive={}, status={}, accessId={}",
currentUserId, access.getAccessLevel(), access.getIsActive(), access.getStatus(), access.getId());


boolean isManager = access.getAccessLevel() == AccessLevel.MANAGER;
boolean isActive = Boolean.TRUE.equals(access.getIsActive());

if (!isManager) {
log.error("Storekeeper {} does not have MANAGER-level access to warehouse {}. Current level: {}",
currentUserId, warehouseId, access.getAccessLevel());
throw new UnauthorizedException("Only storekeepers assigned as MANAGER to this warehouse can approve/reject requests");
}

if (!isActive) {
log.error("Storekeeper {} has MANAGER access to warehouse {} but it is not active. Status: {}, isActive: {}",
currentUserId, warehouseId, access.getStatus(), access.getIsActive());
throw new UnauthorizedException("Your warehouse assignment is not active. Please contact an administrator.");
}

log.info("Storekeeper {} authorized to approve/reject requests for warehouse {}", currentUserId, warehouseId);
} else {

List<WarehouseAccess> allAccesses = warehouseAccessRepository.findByUserId(currentUserId);
log.error("No warehouse access found for storekeeper {} to warehouse {}. Storekeeper has {} total accesses: {}",
currentUserId, warehouseId, allAccesses.size(),
allAccesses.stream()
.map(wa -> {
try {
Long waWarehouseId = wa.getWarehouse() != null ? wa.getWarehouse().getId() : null;
return String.format("warehouseId=%s, level=%s, active=%s, status=%s",
waWarehouseId,
wa.getAccessLevel(),
wa.getIsActive(),
wa.getStatus());
} catch (Exception e) {
return "error accessing access: " + e.getMessage();
}
})
.toList());


List<WarehouseAccess> managerAccesses = allAccesses.stream()
.filter(wa -> wa.getAccessLevel() == AccessLevel.MANAGER)
.toList();
if (!managerAccesses.isEmpty()) {
log.warn("Storekeeper {} has MANAGER access to {} warehouse(s), but not to warehouse {}",
currentUserId, managerAccesses.size(), warehouseId);
}

throw new UnauthorizedException("You are not assigned to this warehouse. Please contact an administrator to be assigned.");
}
} else if (currentUser.getUserType() == UserType.ADMIN) {

throw new UnauthorizedException("Admins cannot approve/reject warehouse access requests. Only storekeepers can manage requests for their assigned warehouses.");
} else {
throw new UnauthorizedException("Only storekeepers can approve/reject warehouse access requests");
}
}

com.raf.enums.WarehouseAccessStatus oldStatus = warehouseAccess.getStatus();
warehouseAccess.setStatus(status);
if (status == com.raf.enums.WarehouseAccessStatus.APPROVED || status == com.raf.enums.WarehouseAccessStatus.ACTIVE) {
warehouseAccess.setIsActive(true);
} else if (status == com.raf.enums.WarehouseAccessStatus.REJECTED) {
warehouseAccess.setIsActive(false);
}

WarehouseAccess savedAccess = warehouseAccessRepository.save(warehouseAccess);
entityManager.flush();


if ((status == com.raf.enums.WarehouseAccessStatus.APPROVED || status == com.raf.enums.WarehouseAccessStatus.ACTIVE)
&& !oldStatus.equals(status)
&& warehouseAccess.getCropType() != null
&& warehouseAccess.getCropQuantityKg() != null
&& warehouseAccess.getCropQuantityKg().compareTo(java.math.BigDecimal.ZERO) > 0) {

try {
log.info("Warehouse access approved with crop information. Creating inventory automatically for warehouse access ID: {}", id);



List<Inventory> existingInventories = inventoryRepository.findByFarmerId(warehouseAccess.getUser().getId());
boolean inventoryExists = existingInventories.stream()
.anyMatch(inv -> inv.getWarehouse().getId().equals(warehouseAccess.getWarehouse().getId())
&& inv.getCropType().getId().equals(warehouseAccess.getCropType().getId())
&& inv.getStatus() == InventoryStatus.STORED);

if (!inventoryExists) {

String inventoryCode = "INV-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();


User storekeeper = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found with ID: " + currentUserId));


Inventory inventory = new Inventory();
inventory.setInventoryCode(inventoryCode);
inventory.setFarmer(warehouseAccess.getUser());
inventory.setWarehouse(warehouseAccess.getWarehouse());
inventory.setCropType(warehouseAccess.getCropType());
inventory.setStorekeeper(storekeeper);
inventory.setQuantityKg(warehouseAccess.getCropQuantityKg());
inventory.setRemainingQuantityKg(warehouseAccess.getCropQuantityKg());
inventory.setQualityGrade(warehouseAccess.getQualityGrade() != null ? warehouseAccess.getQualityGrade() : "A");
inventory.setStorageDate(warehouseAccess.getExpectedStorageDate() != null ?
warehouseAccess.getExpectedStorageDate() : LocalDate.now());
inventory.setExpectedWithdrawalDate(warehouseAccess.getExpectedWithdrawalDate());
inventory.setStatus(InventoryStatus.STORED);
inventory.setNotes(warehouseAccess.getCropNotes());
if (warehouseAccess.getCropImageUrl() != null && !warehouseAccess.getCropImageUrl().isEmpty()) {
inventory.setCropImageUrl(warehouseAccess.getCropImageUrl());
}

Inventory savedInventory = inventoryRepository.save(inventory);
entityManager.flush();

log.info("Created inventory with ID: {}, Code: {} for farmer {} in warehouse {}",
savedInventory.getId(),
savedInventory.getInventoryCode(),
warehouseAccess.getUser().getId(),
warehouseAccess.getWarehouse().getId());


StorageWarehouse warehouse = warehouseAccess.getWarehouse();
java.math.BigDecimal newAvailableCapacity = warehouse.getAvailableCapacityKg()
.subtract(warehouseAccess.getCropQuantityKg());
if (newAvailableCapacity.compareTo(java.math.BigDecimal.ZERO) < 0) {
log.warn("Warning: Warehouse capacity would go negative. Setting to 0.");
newAvailableCapacity = java.math.BigDecimal.ZERO;
}
warehouse.setAvailableCapacityKg(newAvailableCapacity);
warehouseRepository.save(warehouse);
entityManager.flush();

log.info("Successfully created inventory {} from warehouse access request {}", inventoryCode, id);


try {
notificationService.createNotification(
warehouseAccess.getUser().getId(),
"Inventory Created",
String.format("Your inventory has been created in %s. Inventory Code: %s",
warehouse.getWarehouseName(), inventoryCode),
com.raf.enums.NotificationType.INVENTORY_CREATED,
null,
"/inventory"
);
} catch (Exception e) {
log.error("Failed to create notification for inventory creation: {}", e.getMessage(), e);
}
} else {
log.info("Inventory already exists for this warehouse access. Skipping creation.");
}
} catch (Exception e) {
log.error("Failed to create inventory from warehouse access request {}: {}", id, e.getMessage(), e);


}
}


if (!oldStatus.equals(status)) {
com.raf.enums.NotificationType notificationType;
String title;
String message;

if (status == com.raf.enums.WarehouseAccessStatus.APPROVED || status == com.raf.enums.WarehouseAccessStatus.ACTIVE) {
notificationType = com.raf.enums.NotificationType.WAREHOUSE_ACCESS_APPROVED;
title = "Warehouse Access Approved";
String warehouseName = warehouseAccess.getWarehouse() != null
? warehouseAccess.getWarehouse().getWarehouseName()
: "the warehouse";
message = String.format("Your warehouse access request for %s has been APPROVED. You can now store your crops in this warehouse.",
warehouseName);
} else if (status == com.raf.enums.WarehouseAccessStatus.REJECTED) {
notificationType = com.raf.enums.NotificationType.WAREHOUSE_ACCESS_REJECTED;
title = "Warehouse Access Rejected";
String warehouseName = warehouseAccess.getWarehouse() != null
? warehouseAccess.getWarehouse().getWarehouseName()
: "the warehouse";
message = String.format("Your warehouse access request for %s has been REJECTED. Please contact the warehouse manager for more information.",
warehouseName);
} else {

notificationType = com.raf.enums.NotificationType.SYSTEM_ALERT;
title = "Warehouse Access Status Updated";
String warehouseName = warehouseAccess.getWarehouse() != null
? warehouseAccess.getWarehouse().getWarehouseName()
: "the warehouse";
message = String.format("Your warehouse access request for %s status has been updated to: %s",
warehouseName, status);
}


if (warehouseAccess.getUser() != null && warehouseAccess.getUser().getId() != null) {
try {
notificationService.createNotification(
warehouseAccess.getUser().getId(),
title,
message,
notificationType,
null,
"/farmer/dashboard"
);
} catch (Exception e) {
log.error("Failed to create notification for warehouse access status change: {}", e.getMessage(), e);

}
} else {
log.warn("Cannot create notification: user or user ID is null for warehouse access {}", id);
}

log.info("Created notification for warehouse access status change: {} -> {}", oldStatus, status);
}

log.info("Updating warehouse access ID {} status to: {}", id, status);
return savedAccess;
}


public com.raf.dto.StorekeeperAssignmentResponse checkStorekeeperAssignment(Long storekeeperId, Long warehouseId) {
log.info("Checking storekeeper assignment for storekeeper {} to warehouse {}", storekeeperId, warehouseId);

User storekeeper = userRepository.findById(storekeeperId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found with ID: " + storekeeperId));

if (storekeeper.getUserType() != UserType.STOREKEEPER) {
throw new UnauthorizedException("User is not a storekeeper");
}

StorageWarehouse warehouse = warehouseRepository.findById(warehouseId)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));


List<WarehouseAccess> existingManagerAccesses = warehouseAccessRepository.findByUserId(storekeeperId)
.stream()
.filter(wa -> wa.getAccessLevel() == AccessLevel.MANAGER && wa.getIsActive())
.filter(wa -> !wa.getWarehouse().getId().equals(warehouseId))
.toList();

if (!existingManagerAccesses.isEmpty()) {
WarehouseAccess existingAccess = existingManagerAccesses.get(0);
String existingWarehouseName = existingAccess.getWarehouse().getWarehouseName();

return com.raf.dto.StorekeeperAssignmentResponse.builder()
.requiresConfirmation(true)
.existingWarehouseName(existingWarehouseName)
.message(String.format("This storekeeper is already assigned to warehouse '%s'. Do you want to replace it?", existingWarehouseName))
.build();
}

return com.raf.dto.StorekeeperAssignmentResponse.builder()
.requiresConfirmation(false)
.message("Storekeeper can be assigned to this warehouse")
.build();
}


public WarehouseAccess assignStorekeeperToWarehouse(Long storekeeperId, Long warehouseId) {
log.info("Assigning storekeeper {} to warehouse {}", storekeeperId, warehouseId);

User storekeeper = userRepository.findById(storekeeperId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found with ID: " + storekeeperId));

if (storekeeper.getUserType() != UserType.STOREKEEPER) {
throw new UnauthorizedException("User is not a storekeeper");
}

StorageWarehouse warehouse = warehouseRepository.findById(warehouseId)
.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));



List<WarehouseAccess> existingManagerAccesses = warehouseAccessRepository.findByUserId(storekeeperId)
.stream()
.filter(wa -> wa.getAccessLevel() == AccessLevel.MANAGER && Boolean.TRUE.equals(wa.getIsActive()))
.toList();

log.info("Found {} existing MANAGER-level active accesses for storekeeper {}", existingManagerAccesses.size(), storekeeperId);

for (WarehouseAccess oldAccess : existingManagerAccesses) {
Long oldWarehouseId = oldAccess.getWarehouse() != null ? oldAccess.getWarehouse().getId() : null;
if (oldWarehouseId != null && !oldWarehouseId.equals(warehouseId)) {

oldAccess.setIsActive(false);
oldAccess.setStatus(WarehouseAccessStatus.REJECTED);
warehouseAccessRepository.save(oldAccess);
log.info("Deactivated old warehouse assignment for storekeeper {} from warehouse {} (ID: {})",
storekeeperId,
oldAccess.getWarehouse().getWarehouseName(),
oldWarehouseId);
} else if (oldWarehouseId != null && oldWarehouseId.equals(warehouseId)) {

log.info("Storekeeper {} already has access to warehouse {} (ID: {}), will update it",
storekeeperId, oldAccess.getWarehouse().getWarehouseName(), oldWarehouseId);
}
}


entityManager.flush();


Optional<WarehouseAccess> existingAccessOpt = warehouseAccessRepository.findByUserIdAndWarehouseId(storekeeperId, warehouseId);
if (existingAccessOpt.isPresent()) {
WarehouseAccess existing = existingAccessOpt.get();


existing.setAccessLevel(AccessLevel.MANAGER);
existing.setStatus(WarehouseAccessStatus.ACTIVE);
existing.setIsActive(true);
existing.setGrantedDate(java.time.LocalDate.now());
WarehouseAccess updated = warehouseAccessRepository.save(existing);
entityManager.flush();
log.info("Updated existing access for storekeeper {} to warehouse {} (access ID: {})",
storekeeperId, warehouseId, updated.getId());


Optional<WarehouseAccess> verify = warehouseAccessRepository.findByUserIdAndWarehouseId(storekeeperId, warehouseId);
if (verify.isPresent() && verify.get().getAccessLevel() == AccessLevel.MANAGER && Boolean.TRUE.equals(verify.get().getIsActive())) {
log.info("Verified: Storekeeper {} now has MANAGER-level active access to warehouse {}", storekeeperId, warehouseId);
} else {
log.error("WARNING: Verification failed after update! Storekeeper {} may not have proper access to warehouse {}",
storekeeperId, warehouseId);
}

return updated;
}


WarehouseAccess access = new WarehouseAccess();
access.setUser(storekeeper);
access.setWarehouse(warehouse);
access.setAccessLevel(AccessLevel.MANAGER);
access.setGrantedDate(java.time.LocalDate.now());
access.setStatus(WarehouseAccessStatus.ACTIVE);
access.setIsActive(true);
access.setNotes("Storekeeper assigned to warehouse by admin");

WarehouseAccess savedAccess = warehouseAccessRepository.save(access);
entityManager.flush();
log.info("Created new access: Assigned storekeeper {} (ID: {}) as MANAGER to warehouse {} (ID: {}) - Access ID: {}",
storekeeper.getEmail(), storekeeperId, warehouse.getWarehouseName(), warehouseId, savedAccess.getId());


Optional<WarehouseAccess> verify = warehouseAccessRepository.findByUserIdAndWarehouseId(storekeeperId, warehouseId);
if (verify.isPresent() && verify.get().getAccessLevel() == AccessLevel.MANAGER && Boolean.TRUE.equals(verify.get().getIsActive())) {
log.info("Verified: Storekeeper {} now has MANAGER-level active access to warehouse {}", storekeeperId, warehouseId);
} else {
log.error("ERROR: Verification failed after creation! Storekeeper {} does not have proper access to warehouse {}. " +
"This is a critical error - the access record may not have been saved correctly.",
storekeeperId, warehouseId);
throw new RuntimeException("Failed to create warehouse access record. Please try again or contact support.");
}

return savedAccess;
}


@Transactional(readOnly = true)
public Map<String, Object> verifyStorekeeperAssignment(Long storekeeperId, Long warehouseId) {
Map<String, Object> result = new HashMap<>();

User storekeeper = userRepository.findById(storekeeperId).orElse(null);
if (storekeeper == null) {
result.put("error", "Storekeeper not found");
return result;
}

result.put("storekeeperId", storekeeperId);
result.put("storekeeperEmail", storekeeper.getEmail());
result.put("userType", storekeeper.getUserType().toString());

StorageWarehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
if (warehouse == null) {
result.put("error", "Warehouse not found");
return result;
}

result.put("warehouseId", warehouseId);
result.put("warehouseName", warehouse.getWarehouseName());

Optional<WarehouseAccess> accessOpt = warehouseAccessRepository.findByUserIdAndWarehouseId(storekeeperId, warehouseId);
if (accessOpt.isPresent()) {
WarehouseAccess access = accessOpt.get();
result.put("hasAccess", true);
result.put("accessLevel", access.getAccessLevel().toString());
result.put("isActive", access.getIsActive());
result.put("status", access.getStatus().toString());
result.put("isManager", access.getAccessLevel() == AccessLevel.MANAGER);
result.put("isActiveAndManager", access.getAccessLevel() == AccessLevel.MANAGER && Boolean.TRUE.equals(access.getIsActive()));
} else {
result.put("hasAccess", false);
result.put("accessLevel", null);
result.put("isActive", null);
result.put("status", null);
result.put("isManager", false);
result.put("isActiveAndManager", false);


List<WarehouseAccess> allAccesses = warehouseAccessRepository.findByUserId(storekeeperId);
List<Map<String, Object>> allAccessesInfo = allAccesses.stream()
.map(wa -> {
Map<String, Object> info = new HashMap<>();
info.put("warehouseId", wa.getWarehouse() != null ? wa.getWarehouse().getId() : null);
info.put("warehouseName", wa.getWarehouse() != null ? wa.getWarehouse().getWarehouseName() : null);
info.put("accessLevel", wa.getAccessLevel().toString());
info.put("isActive", wa.getIsActive());
info.put("status", wa.getStatus().toString());
return info;
})
.toList();
result.put("allStorekeeperAccesses", allAccessesInfo);
}

return result;
}


private void notifyStorekeepers(Long warehouseId, User farmer, WarehouseAccessRequest request) {
try {
log.info("Notifying storekeepers for warehouse ID: {}", warehouseId);



List<WarehouseAccess> allAccesses = warehouseAccessRepository.findByWarehouseId(warehouseId);
log.debug("Found {} total accesses for warehouse {}", allAccesses.size(), warehouseId);

List<WarehouseAccess> storekeeperAccesses = allAccesses.stream()
.filter(wa -> {
try {

User user = wa.getUser();
if (user == null) {
log.debug("WarehouseAccess {} has null user", wa.getId());
return false;
}

boolean isStorekeeper = user.getUserType() == UserType.STOREKEEPER;
boolean isManager = wa.getAccessLevel() == AccessLevel.MANAGER;
boolean isActive = wa.getIsActive() != null && wa.getIsActive();

log.debug("Checking access {}: userType={}, accessLevel={}, isActive={}, userId={}",
wa.getId(), user.getUserType(), wa.getAccessLevel(), isActive, user.getId());

return isStorekeeper && isManager && isActive;
} catch (Exception e) {
log.warn("Error checking warehouse access {}: {}", wa.getId(), e.getMessage());
return false;
}
})
.toList();

log.info("Found {} storekeeper(s) to notify for warehouse {}", storekeeperAccesses.size(), warehouseId);


if (storekeeperAccesses.isEmpty()) {
log.debug("No storekeepers found with first method, trying direct user lookup...");
List<User> allStorekeepers = userRepository.findByUserType(UserType.STOREKEEPER);
log.debug("Found {} total storekeepers in system", allStorekeepers.size());

for (User storekeeper : allStorekeepers) {
try {

Optional<WarehouseAccess> access = warehouseAccessRepository
.findByUserIdAndWarehouseId(storekeeper.getId(), warehouseId);

if (access.isPresent()) {
WarehouseAccess wa = access.get();
if (wa.getAccessLevel() == AccessLevel.MANAGER &&
(wa.getIsActive() == null || wa.getIsActive())) {
storekeeperAccesses = List.of(wa);
log.info("Found storekeeper {} via direct lookup", storekeeper.getEmail());
break;
}
}
} catch (Exception e) {
log.warn("Error checking storekeeper {}: {}", storekeeper.getEmail(), e.getMessage());
}
}
}


for (WarehouseAccess storekeeperAccess : storekeeperAccesses) {
try {
User storekeeper = storekeeperAccess.getUser();
if (storekeeper == null || storekeeper.getId() == null) {
log.warn("Storekeeper user is null or has no ID, skipping notification");
continue;
}

String cropInfo = "";
if (request.getCropTypeId() != null && request.getCropQuantityKg() != null) {
com.raf.entity.CropType cropType = cropTypeRepository.findById(request.getCropTypeId()).orElse(null);
if (cropType != null) {
cropInfo = String.format(" for %s kg of %s", request.getCropQuantityKg(), cropType.getCropName());
}
}

notificationService.createNotification(
storekeeper.getId(),
"New Warehouse Access Request",
String.format("Farmer %s %s has submitted a warehouse access request%s for %s. Please review and respond.",
farmer.getFirstName(), farmer.getLastName(), cropInfo, storekeeperAccess.getWarehouse().getWarehouseName()),
com.raf.enums.NotificationType.WAREHOUSE_ACCESS_SUBMITTED,
null,
"/storekeeper/dashboard"
);
log.info("Successfully notified storekeeper {} (ID: {}) about new warehouse access request",
storekeeper.getEmail(), storekeeper.getId());
} catch (Exception e) {
log.error("Failed to notify storekeeper {} about warehouse access request: {}",
storekeeperAccess.getUser() != null ? storekeeperAccess.getUser().getEmail() : "unknown",
e.getMessage(), e);

}
}

if (storekeeperAccesses.isEmpty()) {
log.warn("No storekeepers found to notify for warehouse ID: {}. This may be normal if no storekeeper is assigned yet.", warehouseId);
}
} catch (Exception e) {
log.error("Failed to notify storekeepers about warehouse access request: {}", e.getMessage(), e);

}
}
}