package com.raf.service;

import com.raf.dto.InventoryRequestCreateDto;
import com.raf.dto.InventoryRequestResponseDto;
import com.raf.entity.Inventory;
import com.raf.entity.InventoryRequest;
import com.raf.entity.User;
import com.raf.enums.InventoryRequestStatus;
import com.raf.enums.InventoryRequestType;
import com.raf.enums.InventoryStatus;
import com.raf.enums.UserType;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.InventoryRepository;
import com.raf.repository.InventoryRequestRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import com.raf.entity.WarehouseAccess;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryRequestService {

private final InventoryRequestRepository inventoryRequestRepository;
private final InventoryRepository inventoryRepository;
private final UserRepository userRepository;
private final CropTypeRepository cropTypeRepository;
private final NotificationService notificationService;
private final WarehouseAccessRepository warehouseAccessRepository;

@PersistenceContext
private EntityManager entityManager;


public InventoryRequest createRequest(Long farmerId, InventoryRequestCreateDto dto) {
log.info("Farmer {} creating inventory request for inventory {}", farmerId, dto.getInventoryId());


User farmer = userRepository.findById(farmerId)
.orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + farmerId));
if (farmer.getUserType() != UserType.FARMER) {
throw new IllegalArgumentException("Only farmers can create inventory requests");
}


Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + dto.getInventoryId()));

if (!inventory.getFarmer().getId().equals(farmerId)) {
throw new IllegalArgumentException("You can only create requests for your own inventory");
}


User storekeeper = inventory.getStorekeeper();
if (storekeeper == null) {
throw new IllegalArgumentException("No storekeeper assigned to this inventory");
}


String normalizedImageUrl = (dto.getNewCropImageUrl() != null && dto.getNewCropImageUrl().trim().isEmpty())
? null : dto.getNewCropImageUrl();
String normalizedNotes = (dto.getNewNotes() != null && dto.getNewNotes().trim().isEmpty())
? null : dto.getNewNotes();


if (dto.getRequestType() == InventoryRequestType.UPDATE) {
if (normalizedImageUrl == null && dto.getNewPricePerKg() == null && normalizedNotes == null) {
throw new IllegalArgumentException("UPDATE request must include at least one field to update (image, price, or notes)");
}
} else if (dto.getRequestType() == InventoryRequestType.WITHDRAWAL) {
if (dto.getWithdrawalQuantityKg() == null || dto.getWithdrawalQuantityKg().compareTo(BigDecimal.ZERO) <= 0) {
throw new IllegalArgumentException("WITHDRAWAL request must specify withdrawal quantity");
}
if (dto.getWithdrawalQuantityKg().compareTo(inventory.getRemainingQuantityKg()) > 0) {
throw new IllegalArgumentException("Withdrawal quantity cannot exceed remaining quantity");
}
}


String requestCode = "INV-REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
while (inventoryRequestRepository.findByRequestCode(requestCode).isPresent()) {
requestCode = "INV-REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}


InventoryRequest request = new InventoryRequest();
request.setRequestCode(requestCode);
request.setInventory(inventory);
request.setFarmer(farmer);
request.setStorekeeper(storekeeper);
request.setRequestType(dto.getRequestType());
request.setStatus(InventoryRequestStatus.PENDING);
request.setNewCropImageUrl(normalizedImageUrl);
request.setNewPricePerKg(dto.getNewPricePerKg());
request.setNewNotes(normalizedNotes);
request.setWithdrawalQuantityKg(dto.getWithdrawalQuantityKg());
request.setWithdrawalDate(dto.getWithdrawalDate());
request.setFarmerNotes((dto.getFarmerNotes() != null && dto.getFarmerNotes().trim().isEmpty())
? null : dto.getFarmerNotes());

InventoryRequest savedRequest = inventoryRequestRepository.save(request);
entityManager.flush();


try {
notificationService.createNotification(
storekeeper.getId(),
"New Inventory Request",
String.format("Farmer %s %s has submitted a %s request for inventory %s",
farmer.getFirstName(), farmer.getLastName(),
dto.getRequestType().name(),
inventory.getInventoryCode()),
com.raf.enums.NotificationType.INVENTORY_UPDATED,
null,
"/storekeeper/dashboard"
);
} catch (Exception e) {
log.error("Failed to send notification to storekeeper: {}", e.getMessage());
}

log.info("✅ Successfully created inventory request {} for inventory {}", requestCode, dto.getInventoryId());
return savedRequest;
}


public InventoryRequest respondToRequest(Long requestId, Long storekeeperId, InventoryRequestResponseDto dto) {
log.info("Storekeeper {} responding to inventory request {}", storekeeperId, requestId);

InventoryRequest request = inventoryRequestRepository.findById(requestId)
.orElseThrow(() -> new ResourceNotFoundException("Inventory request not found with ID: " + requestId));


Long inventoryWarehouseId = request.getInventory().getWarehouse().getId();


boolean hasWarehouseAccess = warehouseAccessRepository.existsByUserIdAndWarehouseIdAndIsActive(
storekeeperId, inventoryWarehouseId, true);


boolean isAssignedStorekeeper = request.getStorekeeper().getId().equals(storekeeperId);

if (!hasWarehouseAccess && !isAssignedStorekeeper) {
log.warn("Storekeeper {} attempted to respond to request {} for warehouse {} without access",
storekeeperId, requestId, inventoryWarehouseId);
throw new IllegalArgumentException("Unauthorized: You can only respond to requests for inventories in your assigned warehouses");
}


if (hasWarehouseAccess) {
warehouseAccessRepository.findByUserIdAndWarehouseId(storekeeperId, inventoryWarehouseId)
.ifPresent(access -> {
if (access.getStatus() != com.raf.enums.WarehouseAccessStatus.APPROVED &&
access.getStatus() != com.raf.enums.WarehouseAccessStatus.ACTIVE) {
throw new IllegalArgumentException("Unauthorized: Your warehouse access is not active");
}
});
}

if (request.getStatus() != InventoryRequestStatus.PENDING) {
throw new IllegalArgumentException("Request is not pending and cannot be modified");
}

request.setStorekeeperResponse(dto.getStorekeeperResponse());

if (dto.getApprove()) {

request.setStatus(InventoryRequestStatus.APPROVED);
processApprovedRequest(request);
request.setStatus(InventoryRequestStatus.COMPLETED);
} else {

request.setStatus(InventoryRequestStatus.REJECTED);
}

request.setProcessedDate(java.time.LocalDateTime.now());
InventoryRequest savedRequest = inventoryRequestRepository.save(request);
entityManager.flush();


try {
String message = dto.getApprove()
? String.format("Your %s request for inventory %s has been approved by the storekeeper.",
request.getRequestType().name(), request.getInventory().getInventoryCode())
: String.format("Your %s request for inventory %s has been rejected by the storekeeper.",
request.getRequestType().name(), request.getInventory().getInventoryCode());

notificationService.createNotification(
request.getFarmer().getId(),
dto.getApprove() ? "Request Approved" : "Request Rejected",
message,
com.raf.enums.NotificationType.INVENTORY_UPDATED,
null,
"/farmer/dashboard"
);
} catch (Exception e) {
log.error("Failed to send notification to farmer: {}", e.getMessage());
}

log.info("✅ Storekeeper {} {} request {}", storekeeperId, dto.getApprove() ? "approved" : "rejected", requestId);
return savedRequest;
}


private void processApprovedRequest(InventoryRequest request) {
Inventory inventory = request.getInventory();

if (request.getRequestType() == InventoryRequestType.UPDATE) {

if (request.getNewCropImageUrl() != null && !request.getNewCropImageUrl().trim().isEmpty()) {
inventory.setCropImageUrl(request.getNewCropImageUrl().trim());
log.info("Updated crop image URL for inventory {}", inventory.getId());
}

if (request.getNewPricePerKg() != null && request.getNewPricePerKg().compareTo(BigDecimal.ZERO) > 0) {
if (inventory.getCropType() != null) {
inventory.getCropType().setPricePerKg(request.getNewPricePerKg());
cropTypeRepository.save(inventory.getCropType());
log.info("Updated price for crop type {} to {}",
inventory.getCropType().getCropName(), request.getNewPricePerKg());
}
}

if (request.getNewNotes() != null && !request.getNewNotes().trim().isEmpty()) {
inventory.setNotes(request.getNewNotes().trim());
log.info("Updated notes for inventory {}", inventory.getId());
}

inventoryRepository.save(inventory);

} else if (request.getRequestType() == InventoryRequestType.WITHDRAWAL) {

BigDecimal remaining = inventory.getRemainingQuantityKg().subtract(request.getWithdrawalQuantityKg());

if (remaining.compareTo(BigDecimal.ZERO) <= 0) {

inventory.setRemainingQuantityKg(BigDecimal.ZERO);
inventory.setStatus(InventoryStatus.WITHDRAWN);
log.info("Fully withdrawn inventory {}", inventory.getId());
} else {

inventory.setRemainingQuantityKg(remaining);
log.info("Partially withdrawn {} from inventory {}", request.getWithdrawalQuantityKg(), inventory.getId());
}

inventoryRepository.save(inventory);
}

entityManager.flush();
}

public List<InventoryRequest> getRequestsByFarmer(Long farmerId) {
return inventoryRequestRepository.findByFarmerId(farmerId);
}


public List<InventoryRequest> getRequestsByStorekeeper(Long storekeeperId) {
log.info("Getting inventory requests for storekeeper {} (filtered by assigned warehouses)", storekeeperId);


List<WarehouseAccess> warehouseAccesses = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true);


List<Long> assignedWarehouseIds = warehouseAccesses.stream()
.filter(access -> access.getStatus() == com.raf.enums.WarehouseAccessStatus.APPROVED ||
access.getStatus() == com.raf.enums.WarehouseAccessStatus.ACTIVE)
.map(access -> access.getWarehouse().getId())
.distinct()
.toList();

if (assignedWarehouseIds.isEmpty()) {
log.info("Storekeeper {} has no assigned warehouses, returning empty list", storekeeperId);
return List.of();
}

log.info("Storekeeper {} has {} assigned warehouses: {}", storekeeperId, assignedWarehouseIds.size(), assignedWarehouseIds);


List<InventoryRequest> allRequests = inventoryRequestRepository.findByStorekeeperId(storekeeperId);


List<InventoryRequest> filteredRequests = allRequests.stream()
.filter(request -> {
Long warehouseId = request.getInventory().getWarehouse().getId();
boolean matches = assignedWarehouseIds.contains(warehouseId);
if (!matches) {
log.debug("Filtering out request {} - warehouse {} not in assigned warehouses",
request.getRequestCode(), warehouseId);
}
return matches;
})
.toList();

log.info("Returning {} inventory requests for storekeeper {} (filtered from {} total)",
filteredRequests.size(), storekeeperId, allRequests.size());

return filteredRequests;
}


public List<InventoryRequest> getPendingRequestsByStorekeeper(Long storekeeperId) {
log.info("Getting pending inventory requests for storekeeper {} (filtered by assigned warehouses)", storekeeperId);


List<WarehouseAccess> warehouseAccesses = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true);


List<Long> assignedWarehouseIds = warehouseAccesses.stream()
.filter(access -> access.getStatus() == com.raf.enums.WarehouseAccessStatus.APPROVED ||
access.getStatus() == com.raf.enums.WarehouseAccessStatus.ACTIVE)
.map(access -> access.getWarehouse().getId())
.distinct()
.toList();

if (assignedWarehouseIds.isEmpty()) {
log.info("Storekeeper {} has no assigned warehouses, returning empty list", storekeeperId);
return List.of();
}

log.info("Storekeeper {} has {} assigned warehouses: {}", storekeeperId, assignedWarehouseIds.size(), assignedWarehouseIds);


List<InventoryRequest> allPendingRequests = inventoryRequestRepository.findByStorekeeperIdAndStatus(
storekeeperId, InventoryRequestStatus.PENDING);


List<InventoryRequest> filteredRequests = allPendingRequests.stream()
.filter(request -> {
Long warehouseId = request.getInventory().getWarehouse().getId();
boolean matches = assignedWarehouseIds.contains(warehouseId);
if (!matches) {
log.debug("Filtering out pending request {} - warehouse {} not in assigned warehouses",
request.getRequestCode(), warehouseId);
}
return matches;
})
.toList();

log.info("Returning {} pending inventory requests for storekeeper {} (filtered from {} total)",
filteredRequests.size(), storekeeperId, allPendingRequests.size());

return filteredRequests;
}

public InventoryRequest getRequestById(Long id) {
return inventoryRequestRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Inventory request not found with ID: " + id));
}
}

