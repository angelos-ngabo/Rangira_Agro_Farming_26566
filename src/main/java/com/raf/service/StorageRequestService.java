package com.raf.service;

import com.raf.dto.StorageRequestDto;
import com.raf.entity.*;
import com.raf.enums.AccessLevel;
import com.raf.enums.InventoryStatus;
import com.raf.enums.WarehouseAccessStatus;
import com.raf.exception.ResourceNotFoundException;
import com.raf.exception.UnauthorizedException;
import com.raf.repository.*;
import com.raf.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StorageRequestService {

private final WarehouseAccessRepository warehouseAccessRepository;
private final UserRepository userRepository;
private final StorageWarehouseRepository warehouseRepository;
private final CropTypeRepository cropTypeRepository;
private final InventoryRepository inventoryRepository;
private final JwtUtil jwtUtil;


public WarehouseAccess submitStorageRequest(String token, StorageRequestDto request) {
Long farmerId = jwtUtil.getUserIdFromToken(token);
User farmer = userRepository.findById(farmerId)
.orElseThrow(() -> new ResourceNotFoundException("User not found"));

if (farmer.getUserType() != com.raf.enums.UserType.FARMER) {
throw new UnauthorizedException("Only farmers can submit storage requests");
}

StorageWarehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));

CropType cropType = cropTypeRepository.findById(request.getCropTypeId())
.orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + request.getCropTypeId()));


if (!warehouse.getSupportedCropTypes().contains(cropType)) {
throw new UnauthorizedException("Warehouse does not support storing " + cropType.getCropName());
}


if (warehouse.getAvailableCapacityKg().compareTo(request.getQuantityKg()) < 0) {
throw new UnauthorizedException("Warehouse does not have enough capacity. Available: " +
warehouse.getAvailableCapacityKg() + " kg, Requested: " + request.getQuantityKg() + " kg");
}


WarehouseAccess access = new WarehouseAccess();
access.setUser(farmer);
access.setWarehouse(warehouse);
access.setAccessLevel(AccessLevel.VIEWER);
access.setGrantedDate(LocalDate.now());
access.setStatus(WarehouseAccessStatus.PENDING);
access.setIsActive(false);
access.setRequestedCapacityKg(request.getQuantityKg());
access.setCropType(cropType);
access.setCropQuantityKg(request.getQuantityKg());
access.setQualityGrade(request.getQualityGrade());
access.setExpectedStorageDate(request.getExpectedStorageDate() != null ?
request.getExpectedStorageDate() : LocalDate.now().plusDays(7));
access.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
access.setCropNotes(request.getNotes());
access.setNotes("Storage request from farmer: " + farmer.getEmail());

WarehouseAccess savedRequest = warehouseAccessRepository.save(access);
log.info("Farmer {} submitted storage request for {} kg of {} in warehouse {}",
farmer.getEmail(), request.getQuantityKg(), cropType.getCropName(), warehouse.getWarehouseName());

return savedRequest;
}


@Transactional(readOnly = true)
public List<WarehouseAccess> getPendingStorageRequests() {
return warehouseAccessRepository.findByStatus(WarehouseAccessStatus.PENDING);
}


@Transactional(readOnly = true)
public List<WarehouseAccess> getFarmerStorageRequests(String token) {
Long farmerId = jwtUtil.getUserIdFromToken(token);
return warehouseAccessRepository.findByUserIdAndStatus(farmerId, WarehouseAccessStatus.PENDING);
}


public Inventory approveStorageRequest(Long requestId, Long storekeeperId) {
WarehouseAccess request = warehouseAccessRepository.findById(requestId)
.orElseThrow(() -> new ResourceNotFoundException("Storage request not found with ID: " + requestId));

if (request.getStatus() != WarehouseAccessStatus.PENDING) {
throw new UnauthorizedException("Only pending requests can be approved");
}

User storekeeper = userRepository.findById(storekeeperId)
.orElseThrow(() -> new ResourceNotFoundException("Storekeeper not found with ID: " + storekeeperId));

if (storekeeper.getUserType() != com.raf.enums.UserType.STOREKEEPER) {
throw new UnauthorizedException("User is not a storekeeper");
}


boolean hasAccess = warehouseAccessRepository.existsByUserIdAndWarehouseIdAndIsActive(
storekeeperId, request.getWarehouse().getId(), true);
if (!hasAccess) {
throw new UnauthorizedException("Storekeeper does not have access to this warehouse");
}

StorageWarehouse warehouse = request.getWarehouse();
BigDecimal quantity = request.getCropQuantityKg();


if (warehouse.getAvailableCapacityKg().compareTo(quantity) < 0) {
throw new UnauthorizedException("Warehouse does not have enough capacity");
}


String inventoryCode = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
Inventory inventory = new Inventory();
inventory.setInventoryCode(inventoryCode);
inventory.setFarmer(request.getUser());
inventory.setWarehouse(warehouse);
inventory.setCropType(request.getCropType());
inventory.setStorekeeper(storekeeper);
inventory.setQuantityKg(quantity);
inventory.setRemainingQuantityKg(quantity);
inventory.setQualityGrade(request.getQualityGrade());
inventory.setStorageDate(request.getExpectedStorageDate() != null ?
request.getExpectedStorageDate() : LocalDate.now());
inventory.setExpectedWithdrawalDate(request.getExpectedWithdrawalDate());
inventory.setStatus(InventoryStatus.STORED);
inventory.setNotes(request.getCropNotes());

Inventory savedInventory = inventoryRepository.save(inventory);


warehouse.setAvailableCapacityKg(warehouse.getAvailableCapacityKg().subtract(quantity));
warehouseRepository.save(warehouse);


request.setStatus(WarehouseAccessStatus.ACTIVE);
request.setIsActive(true);
warehouseAccessRepository.save(request);

log.info("Admin approved storage request {} and created inventory {}", requestId, inventoryCode);

return savedInventory;
}


public WarehouseAccess rejectStorageRequest(Long requestId, String reason) {
WarehouseAccess request = warehouseAccessRepository.findById(requestId)
.orElseThrow(() -> new ResourceNotFoundException("Storage request not found with ID: " + requestId));

if (request.getStatus() != WarehouseAccessStatus.PENDING) {
throw new UnauthorizedException("Only pending requests can be rejected");
}

request.setStatus(WarehouseAccessStatus.REJECTED);
request.setIsActive(false);
request.setNotes((request.getNotes() != null ? request.getNotes() + " | " : "") +
"Rejected: " + reason);
warehouseAccessRepository.save(request);

log.info("Admin rejected storage request {}: {}", requestId, reason);

return request;
}
}

