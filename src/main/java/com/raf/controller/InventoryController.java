package com.raf.controller;

import com.raf.dto.InventoryRequest;
import com.raf.entity.Inventory;
import com.raf.enums.InventoryStatus;
import com.raf.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {
private final InventoryService inventoryService;
private final com.raf.util.JwtUtil jwtUtil;
private final com.raf.service.UserService userService;

@PostMapping
@Operation(summary = "Create new inventory - use InventoryRequest with IDs", description = "FARMER ONLY - Only farmers can create inventory")
public ResponseEntity<Inventory> createInventory(
@Valid @RequestBody InventoryRequest request,
jakarta.servlet.http.HttpServletRequest httpRequest) {

Long currentUserId = getCurrentUserId(httpRequest);
com.raf.entity.User user = userService.getUserById(currentUserId);

if (user == null || user.getUserType() != com.raf.enums.UserType.FARMER) {
log.warn("Unauthorized inventory creation attempt by user {} (type: {})",
currentUserId, user != null ? user.getUserType() : "null");
throw new com.raf.exception.UnauthorizedException("Only farmers can create inventory. Your role: " +
(user != null ? user.getUserType() : "unknown"));
}


if (!request.getFarmerId().equals(currentUserId)) {
log.warn("Farmer {} attempted to create inventory for different farmer {}", currentUserId, request.getFarmerId());
throw new com.raf.exception.UnauthorizedException("You can only create inventory for yourself");
}

log.info("✅ Farmer {} creating inventory", currentUserId);
Inventory createdInventory = inventoryService.createInventoryFromRequest(request);
return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
}

@GetMapping
@Operation(summary = "Get all inventories with optional filtering and pagination")
public ResponseEntity<?> getAllInventories(
@RequestParam(required = false) Long farmerId,
@RequestParam(required = false) Long warehouseId,
@RequestParam(required = false) Long cropTypeId,
@RequestParam(required = false) InventoryStatus status,
@RequestParam(required = false) Integer page,
@RequestParam(required = false) Integer size) {


if (page != null && size != null) {
PageRequest pageRequest = PageRequest.of(page, size, Sort.by("storageDate").descending());

if (status != null) {
Page<Inventory> inventories = inventoryService.getInventoriesByStatus(status, pageRequest);
return ResponseEntity.ok(inventories);
} else if (farmerId != null) {
Page<Inventory> inventories = inventoryService.getInventoriesByFarmer(farmerId, pageRequest);
return ResponseEntity.ok(inventories);
} else if (warehouseId != null) {
Page<Inventory> inventories = inventoryService.getInventoriesByWarehouse(warehouseId, pageRequest);
return ResponseEntity.ok(inventories);
} else {
Page<Inventory> inventories = inventoryService.getInventoriesPaginated(pageRequest);
return ResponseEntity.ok(inventories);
}
}


log.info("🌐 API Request: GET /api/inventories - Fetching inventories (farmerId: {}, warehouseId: {}, cropTypeId: {}, status: {})",
farmerId, warehouseId, cropTypeId, status);
List<Inventory> inventories;


Long currentStorekeeperId = null;
try {
jakarta.servlet.http.HttpServletRequest httpRequest = ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();
currentStorekeeperId = getCurrentUserId(httpRequest);
com.raf.entity.User user = userService.getUserById(currentStorekeeperId);
if (user != null && user.getUserType() == com.raf.enums.UserType.STOREKEEPER && warehouseId != null) {

inventories = inventoryService.getInventoriesByWarehouseAndStorekeeper(warehouseId, currentStorekeeperId);
log.info("✅ API Response: Returning {} inventories for storekeeper {} in warehouse {}", inventories.size(), currentStorekeeperId, warehouseId);
return ResponseEntity.ok(inventories);
}
} catch (Exception e) {

}

if (status != null) {
inventories = inventoryService.getInventoriesByStatus(status);
} else if (farmerId != null) {
inventories = inventoryService.getInventoriesByFarmer(farmerId);
} else if (warehouseId != null) {
inventories = inventoryService.getInventoriesByWarehouse(warehouseId);
} else if (cropTypeId != null) {
inventories = inventoryService.getInventoriesByCropType(cropTypeId);
} else {
inventories = inventoryService.getAllInventories();
}

log.info("✅ API Response: Returning {} inventories to client", inventories.size());
return ResponseEntity.ok(inventories);
}

@GetMapping("/paginated")
@Operation(summary = "Get inventories with pagination")
public ResponseEntity<Page<Inventory>> getInventoriesPaginated(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size,
@RequestParam(required = false) InventoryStatus status) {

PageRequest pageRequest = PageRequest.of(page, size, Sort.by("storageDate").descending());

if (status != null) {
Page<Inventory> inventories = inventoryService.getInventoriesByStatus(status, pageRequest);
return ResponseEntity.ok(inventories);
}

Page<Inventory> inventories = inventoryService.getInventoriesPaginated(pageRequest);
return ResponseEntity.ok(inventories);
}

@GetMapping("/available")
@Operation(summary = "Get all available (stored) inventories for buyers")
public ResponseEntity<List<Inventory>> getAvailableInventories() {
List<Inventory> inventories = inventoryService.getAvailableInventoriesForBuyers();
log.info("✅ Returning {} available inventories for buyers", inventories.size());
return ResponseEntity.ok(inventories);
}

@GetMapping("/{id}")
@Operation(summary = "Get inventory by ID")
public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
Inventory inventory = inventoryService.getInventoryById(id);
return ResponseEntity.ok(inventory);
}

@GetMapping("/code/{code}")
@Operation(summary = "Get inventory by code")
public ResponseEntity<Inventory> getInventoryByCode(@PathVariable String code) {
Inventory inventory = inventoryService.getInventoryByCode(code);
return ResponseEntity.ok(inventory);
}

@GetMapping("/farmer/{farmerId}")
@Operation(summary = "Get inventories by farmer")
public ResponseEntity<List<Inventory>> getInventoriesByFarmer(@PathVariable Long farmerId) {
List<Inventory> inventories = inventoryService.getInventoriesByFarmer(farmerId);
return ResponseEntity.ok(inventories);
}

@GetMapping("/warehouse/{warehouseId}")
@Operation(summary = "Get inventories by warehouse")
public ResponseEntity<List<Inventory>> getInventoriesByWarehouse(
@PathVariable Long warehouseId,
@RequestParam(required = false) Long storekeeperId,
jakarta.servlet.http.HttpServletRequest httpRequest) {

Long currentStorekeeperId = storekeeperId;
if (currentStorekeeperId == null) {
try {
currentStorekeeperId = getCurrentUserId(httpRequest);

com.raf.entity.User user = userService.getUserById(currentStorekeeperId);
if (user != null && user.getUserType() == com.raf.enums.UserType.STOREKEEPER) {

List<Inventory> inventories = inventoryService.getInventoriesByWarehouseAndStorekeeper(warehouseId, currentStorekeeperId);
return ResponseEntity.ok(inventories);
}
} catch (Exception e) {

}
} else {

List<Inventory> inventories = inventoryService.getInventoriesByWarehouseAndStorekeeper(warehouseId, currentStorekeeperId);
return ResponseEntity.ok(inventories);
}


List<Inventory> inventories = inventoryService.getInventoriesByWarehouse(warehouseId);
return ResponseEntity.ok(inventories);
}

@GetMapping("/crop-type/{cropTypeId}")
@Operation(summary = "Get inventories by crop type")
public ResponseEntity<List<Inventory>> getInventoriesByCropType(@PathVariable Long cropTypeId) {
List<Inventory> inventories = inventoryService.getInventoriesByCropType(cropTypeId);
return ResponseEntity.ok(inventories);
}

@GetMapping("/status/{status}")
@Operation(summary = "Get inventories by status")
public ResponseEntity<List<Inventory>> getInventoriesByStatus(@PathVariable InventoryStatus status) {
List<Inventory> inventories = inventoryService.getInventoriesByStatus(status);
return ResponseEntity.ok(inventories);
}

@GetMapping("/by-location/{locationCode}")
@Operation(summary = "Get inventories by location code")
public ResponseEntity<List<Inventory>> getInventoriesByLocationCode(@PathVariable String locationCode) {
List<Inventory> inventories = inventoryService.getInventoriesByLocationCode(locationCode);
return ResponseEntity.ok(inventories);
}

@PutMapping("/{id}")
@Operation(summary = "Update inventory")
public ResponseEntity<Inventory> updateInventory(
@PathVariable Long id,
@Valid @RequestBody Inventory inventory) {
Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
return ResponseEntity.ok(updatedInventory);
}

@PatchMapping("/{id}/storekeeper")
@Operation(summary = "Update inventory by storekeeper", description = "Storekeeper only - Update crop image, quantity, or desired price for assigned inventory")
public ResponseEntity<Inventory> updateInventoryByStorekeeper(
@PathVariable Long id,
@Valid @RequestBody com.raf.dto.InventoryUpdateRequest request,
jakarta.servlet.http.HttpServletRequest httpRequest) {
Long storekeeperId = getCurrentUserId(httpRequest);
log.info("🌐 API Request: PATCH /api/inventories/{}/storekeeper - Storekeeper {} updating inventory", id, storekeeperId);
Inventory updatedInventory = inventoryService.updateInventoryByStorekeeper(id, request, storekeeperId);
log.info("✅ API Response: Successfully updated inventory {} by storekeeper {}", id, storekeeperId);
return ResponseEntity.ok(updatedInventory);
}

private Long getCurrentUserId(jakarta.servlet.http.HttpServletRequest request) {
try {
String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
log.error("Authorization header missing or invalid");
throw new RuntimeException("Authorization header missing or invalid");
}

String jwt = authHeader.substring(7);

try {
Long userId = jwtUtil.getUserIdFromToken(jwt);
if (userId != null) {
log.debug("Extracted userId from token: {}", userId);
return userId;
}
} catch (Exception e) {
log.warn("Failed to extract userId from token, will try email fallback: {}", e.getMessage());
}


String email = jwtUtil.extractUsername(jwt);
if (email == null || email.isEmpty()) {
log.error("Unable to extract username from token");
throw new RuntimeException("Unable to extract username from token");
}

log.debug("Extracting userId from email: {}", email);
com.raf.entity.User user = userService.getUserByEmail(email);
if (user == null || user.getId() == null) {
log.error("User not found or has no ID for email: {}", email);
throw new RuntimeException("User not found with email: " + email);
}

log.debug("Successfully got userId: {} for email: {}", user.getId(), email);
return user.getId();
} catch (Exception e) {
log.error("Error extracting current user ID: {}", e.getMessage());
throw new RuntimeException("Unable to extract current user ID", e);
}
}

@PatchMapping("/{id}/reduce")
@Operation(summary = "Reduce inventory quantity (when sold)")
public ResponseEntity<Inventory> reduceInventoryQuantity(
@PathVariable Long id,
@RequestParam BigDecimal quantity) {
Inventory updatedInventory = inventoryService.reduceInventoryQuantity(id, quantity);
return ResponseEntity.ok(updatedInventory);
}

@PatchMapping("/{id}/withdraw")
@Operation(summary = "Mark inventory as withdrawn")
public ResponseEntity<Inventory> withdrawInventory(@PathVariable Long id) {
Inventory updatedInventory = inventoryService.withdrawInventory(id);
return ResponseEntity.ok(updatedInventory);
}

@PostMapping("/storekeeper/create")
@Operation(summary = "Create inventory by storekeeper", description = "Storekeeper only - Create new inventory in assigned warehouse")
public ResponseEntity<Inventory> createInventoryByStorekeeper(
@Valid @RequestBody InventoryRequest request,
jakarta.servlet.http.HttpServletRequest httpRequest) {
Long storekeeperId = getCurrentUserId(httpRequest);
log.info("🌐 API Request: POST /api/inventories/storekeeper/create - Storekeeper {} creating inventory", storekeeperId);
Inventory createdInventory = inventoryService.createInventoryByStorekeeper(request, storekeeperId);
log.info("✅ API Response: Successfully created inventory {} by storekeeper {}", createdInventory.getInventoryCode(), storekeeperId);
return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
}

@DeleteMapping("/{id}")
@Operation(summary = "Delete inventory")
public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
inventoryService.deleteInventory(id);
return ResponseEntity.noContent().build();
}

@DeleteMapping("/{id}/storekeeper")
@Operation(summary = "Delete inventory by storekeeper", description = "Storekeeper only - Delete inventory from assigned warehouse")
public ResponseEntity<Void> deleteInventoryByStorekeeper(
@PathVariable Long id,
jakarta.servlet.http.HttpServletRequest httpRequest) {
Long storekeeperId = getCurrentUserId(httpRequest);
log.info("🌐 API Request: DELETE /api/inventories/{}/storekeeper - Storekeeper {} deleting inventory", id, storekeeperId);
inventoryService.deleteInventoryByStorekeeper(id, storekeeperId);
log.info("✅ API Response: Successfully deleted inventory {} by storekeeper {}", id, storekeeperId);
return ResponseEntity.noContent().build();
}

@GetMapping("/warehouse/{warehouseId}/total-quantity")
@Operation(summary = "Get total stored quantity in warehouse")
public ResponseEntity<BigDecimal> getTotalStoredQuantity(@PathVariable Long warehouseId) {
BigDecimal totalQuantity = inventoryService.getTotalStoredQuantityInWarehouse(warehouseId);
return ResponseEntity.ok(totalQuantity);
}

@GetMapping("/farmer/{farmerId}/active-count")
@Operation(summary = "Count active inventories by farmer")
public ResponseEntity<Long> countActiveInventoriesByFarmer(@PathVariable Long farmerId) {
long count = inventoryService.countActiveInventoriesByFarmer(farmerId);
return ResponseEntity.ok(count);
}

@PostMapping("/cleanup-invalid-farmers")
@Operation(summary = "Clean up inventory items with invalid or missing farmers")
public ResponseEntity<java.util.Map<String, Object>> cleanupInvalidFarmers() {
int deletedCount = inventoryService.cleanupInventoryWithInvalidFarmers();
java.util.Map<String, Object> response = new java.util.HashMap<>();
response.put("deletedCount", deletedCount);
response.put("message", String.format("Successfully deleted %d inventory items with invalid farmers", deletedCount));
return ResponseEntity.ok(response);
}
}

