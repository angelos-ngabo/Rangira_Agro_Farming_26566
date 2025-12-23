package com.raf.controller;

import com.raf.dto.WarehouseCreateRequest;
import com.raf.dto.WarehouseUpdateRequest;
import com.raf.entity.StorageWarehouse;
import com.raf.enums.WarehouseStatus;
import com.raf.enums.WarehouseType;
import com.raf.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouse", description = "Storage warehouse management APIs")
public class WarehouseController {
private final WarehouseService warehouseService;

@PostMapping
@Operation(summary = "Create a new warehouse - use WarehouseUpdateRequest with locationId")
public ResponseEntity<StorageWarehouse> createWarehouse(@Valid @RequestBody WarehouseUpdateRequest request) {
StorageWarehouse createdWarehouse = warehouseService.createWarehouseFromRequest(request);
return new ResponseEntity<>(createdWarehouse, HttpStatus.CREATED);
}

@PostMapping("/with-crop-types")
@Operation(summary = "Create warehouse with crop types", description = "Create a new warehouse specifying which crop types it can store and location (address)")
public ResponseEntity<StorageWarehouse> createWarehouseWithCropTypes(@Valid @RequestBody WarehouseCreateRequest request) {
StorageWarehouse createdWarehouse = warehouseService.createWarehouseWithCropTypes(request);
return new ResponseEntity<>(createdWarehouse, HttpStatus.CREATED);
}

@GetMapping
@Operation(summary = "Get all warehouses with optional filtering")
public ResponseEntity<?> getAllWarehouses(
@RequestParam(required = false) Integer page,
@RequestParam(required = false) Integer size,
@RequestParam(required = false) WarehouseStatus status,
@RequestParam(required = false) Long storekeeperId) {


if (storekeeperId != null) {
List<StorageWarehouse> warehouses = warehouseService.getWarehousesByStorekeeperId(storekeeperId);

if (page != null && size != null) {
int start = page * size;
int end = Math.min(start + size, warehouses.size());
List<StorageWarehouse> paginated = warehouses.subList(Math.min(start, warehouses.size()), end);
Page<StorageWarehouse> pageResult = new org.springframework.data.domain.PageImpl<>(
paginated,
PageRequest.of(page, size),
warehouses.size()
);
return ResponseEntity.ok(pageResult);
}
return ResponseEntity.ok(warehouses);
}


if (page != null && size != null) {
PageRequest pageRequest = PageRequest.of(page, size, Sort.by("warehouseName"));
Page<StorageWarehouse> warehouses;

if (status != null) {
warehouses = warehouseService.getWarehousesByStatus(status, pageRequest);
} else {
warehouses = warehouseService.getWarehousesPaginated(pageRequest);
}
return ResponseEntity.ok(warehouses);
}


if (status != null) {
List<StorageWarehouse> warehouses = warehouseService.getWarehousesByStatus(status);
return ResponseEntity.ok(warehouses);
}


List<StorageWarehouse> warehouses = warehouseService.getAllWarehouses();
return ResponseEntity.ok(warehouses);
}

@GetMapping("/paginated")
@Operation(summary = "Get warehouses with pagination")
public ResponseEntity<Page<StorageWarehouse>> getWarehousesPaginated(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size) {

PageRequest pageRequest = PageRequest.of(page, size, Sort.by("warehouseName"));
Page<StorageWarehouse> warehouses = warehouseService.getWarehousesPaginated(pageRequest);
return ResponseEntity.ok(warehouses);
}

@GetMapping("/available-capacity")
@Operation(summary = "Get warehouses with available capacity")
public ResponseEntity<List<StorageWarehouse>> getWarehousesWithAvailableCapacity() {
List<StorageWarehouse> warehouses = warehouseService.getWarehousesWithAvailableCapacity(BigDecimal.ZERO);
return ResponseEntity.ok(warehouses);
}

@GetMapping("/total-capacity")
@Operation(summary = "Get total available capacity across all active warehouses")
public ResponseEntity<BigDecimal> getTotalAvailableCapacity() {
BigDecimal totalCapacity = warehouseService.getTotalAvailableCapacity();
return ResponseEntity.ok(totalCapacity);
}

@GetMapping("/count")
@Operation(summary = "Get total number of warehouses")
public ResponseEntity<Long> getTotalWarehouses() {
long count = warehouseService.getTotalWarehouses();
return ResponseEntity.ok(count);
}

@GetMapping("/{id}")
@Operation(summary = "Get warehouse by ID")
public ResponseEntity<StorageWarehouse> getWarehouseById(@PathVariable Long id) {
StorageWarehouse warehouse = warehouseService.getWarehouseById(id);
return ResponseEntity.ok(warehouse);
}

@GetMapping("/code/{warehouseCode}")
@Operation(summary = "Get warehouse by code")
public ResponseEntity<StorageWarehouse> getWarehouseByCode(@PathVariable String warehouseCode) {
StorageWarehouse warehouse = warehouseService.getWarehouseByCode(warehouseCode);
return ResponseEntity.ok(warehouse);
}

@GetMapping("/type/{type}")
@Operation(summary = "Get warehouses by type")
public ResponseEntity<List<StorageWarehouse>> getWarehousesByType(@PathVariable WarehouseType type) {
List<StorageWarehouse> warehouses = warehouseService.getWarehousesByType(type);
return ResponseEntity.ok(warehouses);
}

@GetMapping("/status/{status}")
@Operation(summary = "Get warehouses by status")
public ResponseEntity<List<StorageWarehouse>> getWarehousesByStatus(@PathVariable WarehouseStatus status) {
List<StorageWarehouse> warehouses = warehouseService.getWarehousesByStatus(status);
return ResponseEntity.ok(warehouses);
}

@GetMapping("/location/{locationId}")
@Operation(summary = "Get warehouses by location")
public ResponseEntity<List<StorageWarehouse>> getWarehousesByLocation(@PathVariable java.util.UUID locationId) {
List<StorageWarehouse> warehouses = warehouseService.getWarehousesByLocation(locationId);
return ResponseEntity.ok(warehouses);
}

@PutMapping("/{id}")
@Operation(summary = "Update warehouse - use WarehouseUpdateRequest with locationId")
public ResponseEntity<StorageWarehouse> updateWarehouse(
@PathVariable Long id,
@Valid @RequestBody WarehouseUpdateRequest request) {
StorageWarehouse updatedWarehouse = warehouseService.updateWarehouseFromRequest(id, request);
return ResponseEntity.ok(updatedWarehouse);
}

@PatchMapping("/{id}/status")
@Operation(summary = "Update warehouse status")
public ResponseEntity<StorageWarehouse> updateWarehouseStatus(
@PathVariable Long id,
@RequestParam WarehouseStatus status) {
StorageWarehouse updatedWarehouse = warehouseService.updateWarehouseStatus(id, status);
return ResponseEntity.ok(updatedWarehouse);
}

@DeleteMapping("/{id}")
@Operation(summary = "Delete warehouse")
public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
warehouseService.deleteWarehouse(id);
return ResponseEntity.noContent().build();
}

@GetMapping("/exists/code/{warehouseCode}")
@Operation(summary = "Check if warehouse code exists")
public ResponseEntity<Boolean> warehouseCodeExists(@PathVariable String warehouseCode) {
boolean exists = warehouseService.warehouseCodeExists(warehouseCode);
return ResponseEntity.ok(exists);
}
}

