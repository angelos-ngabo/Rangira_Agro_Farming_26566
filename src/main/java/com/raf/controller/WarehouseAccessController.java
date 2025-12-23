package com.raf.controller;

import com.raf.dto.WarehouseAccessRequest;
import com.raf.entity.User;
import com.raf.entity.WarehouseAccess;
import com.raf.enums.AccessLevel;
import com.raf.enums.WarehouseAccessStatus;
import com.raf.service.FileStorageService;
import com.raf.service.UserService;
import com.raf.service.WarehouseAccessService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse-accesses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WarehouseAccess", description = "Warehouse access management APIs")
public class WarehouseAccessController {
private final WarehouseAccessService warehouseAccessService;
private final UserService userService;
private final JwtUtil jwtUtil;
private final FileStorageService fileStorageService;

@PostMapping
@Operation(summary = "Create a new warehouse access - use WarehouseAccessRequest with IDs")
public ResponseEntity<WarehouseAccess> createWarehouseAccess(@Valid @RequestBody WarehouseAccessRequest request) {
WarehouseAccess createdWarehouseAccess = warehouseAccessService.createWarehouseAccessFromRequest(request);
return new ResponseEntity<>(createdWarehouseAccess, HttpStatus.CREATED);
}

@GetMapping
@Operation(summary = "Get all warehouse accesses")
public ResponseEntity<List<WarehouseAccess>> getAllWarehouseAccesses(
@RequestParam(required = false) Long userId,
@RequestParam(required = false) Long warehouseId,
@RequestParam(required = false) WarehouseAccessStatus status,
@RequestParam(required = false) Boolean isActive) {
List<WarehouseAccess> warehouseAccesses;

if (userId != null && warehouseId != null) {
warehouseAccesses = List.of(warehouseAccessService.getWarehouseAccessByUserAndWarehouse(userId, warehouseId));
} else if (userId != null) {
warehouseAccesses = warehouseAccessService.getWarehouseAccessesByUser(userId);
} else if (warehouseId != null) {
if (status != null) {
warehouseAccesses = warehouseAccessService.getWarehouseAccessesByWarehouseAndStatus(warehouseId, status);
} else {
warehouseAccesses = warehouseAccessService.getWarehouseAccessesByWarehouse(warehouseId);
}
} else if (status != null) {
warehouseAccesses = warehouseAccessService.getWarehouseAccessesByStatus(status);
} else {
warehouseAccesses = warehouseAccessService.getAllWarehouseAccesses();
}


if (isActive != null) {
warehouseAccesses = warehouseAccesses.stream()
.filter(wa -> wa.getIsActive() == isActive)
.toList();
}

return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/paginated")
@Operation(summary = "Get warehouse accesses with pagination")
public ResponseEntity<Page<WarehouseAccess>> getWarehouseAccessesPaginated(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size) {

PageRequest pageRequest = PageRequest.of(page, size, Sort.by("grantedDate").descending());
Page<WarehouseAccess> warehouseAccesses = warehouseAccessService.getWarehouseAccessesPaginated(pageRequest);
return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/{id}")
@Operation(summary = "Get warehouse access by ID")
public ResponseEntity<WarehouseAccess> getWarehouseAccessById(@PathVariable Long id) {
WarehouseAccess warehouseAccess = warehouseAccessService.getWarehouseAccessById(id);
return ResponseEntity.ok(warehouseAccess);
}

@GetMapping("/user/{userId}")
@Operation(summary = "Get warehouse accesses by user")
public ResponseEntity<List<WarehouseAccess>> getWarehouseAccessesByUser(@PathVariable Long userId) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getWarehouseAccessesByUser(userId);
return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/warehouse/{warehouseId}")
@Operation(summary = "Get warehouse accesses by warehouse")
public ResponseEntity<List<WarehouseAccess>> getWarehouseAccessesByWarehouse(@PathVariable Long warehouseId) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getWarehouseAccessesByWarehouse(warehouseId);
return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/user/{userId}/warehouse/{warehouseId}")
@Operation(summary = "Get warehouse access for specific user and warehouse")
public ResponseEntity<WarehouseAccess> getWarehouseAccessByUserAndWarehouse(
@PathVariable Long userId,
@PathVariable Long warehouseId) {
WarehouseAccess warehouseAccess = warehouseAccessService.getWarehouseAccessByUserAndWarehouse(userId, warehouseId);
return ResponseEntity.ok(warehouseAccess);
}

@GetMapping("/access-level/{accessLevel}")
@Operation(summary = "Get warehouse accesses by access level")
public ResponseEntity<List<WarehouseAccess>> getWarehouseAccessesByAccessLevel(@PathVariable AccessLevel accessLevel) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getWarehouseAccessesByAccessLevel(accessLevel);
return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/active")
@Operation(summary = "Get active warehouse accesses")
public ResponseEntity<List<WarehouseAccess>> getActiveWarehouseAccesses() {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getActiveWarehouseAccesses();
return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/user/{userId}/active")
@Operation(summary = "Get active warehouse accesses for user")
public ResponseEntity<List<WarehouseAccess>> getActiveWarehouseAccessesForUser(@PathVariable Long userId) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getActiveWarehouseAccessesForUser(userId);
return ResponseEntity.ok(warehouseAccesses);
}

@PutMapping("/{id}")
@Operation(summary = "Update warehouse access")
public ResponseEntity<WarehouseAccess> updateWarehouseAccess(
@PathVariable Long id,
@Valid @RequestBody WarehouseAccessRequest request) {
WarehouseAccess updatedWarehouseAccess = warehouseAccessService.updateWarehouseAccessFromRequest(id, request);
return ResponseEntity.ok(updatedWarehouseAccess);
}

@PatchMapping("/{id}/access-level")
@Operation(summary = "Update access level")
public ResponseEntity<WarehouseAccess> updateAccessLevel(
@PathVariable Long id,
@RequestParam AccessLevel accessLevel) {
WarehouseAccess updatedWarehouseAccess = warehouseAccessService.updateAccessLevel(id, accessLevel);
return ResponseEntity.ok(updatedWarehouseAccess);
}

@PatchMapping("/{id}/revoke")
@Operation(summary = "Revoke warehouse access")
public ResponseEntity<WarehouseAccess> revokeWarehouseAccess(@PathVariable Long id) {
WarehouseAccess revokedWarehouseAccess = warehouseAccessService.revokeWarehouseAccess(id);
return ResponseEntity.ok(revokedWarehouseAccess);
}

@DeleteMapping("/{id}")
@Operation(summary = "Delete warehouse access")
public ResponseEntity<Void> deleteWarehouseAccess(@PathVariable Long id) {
warehouseAccessService.deleteWarehouseAccess(id);
return ResponseEntity.noContent().build();
}

@GetMapping("/check-access/user/{userId}/warehouse/{warehouseId}")
@Operation(summary = "Check if user has access to warehouse")
public ResponseEntity<Boolean> hasAccess(
@PathVariable Long userId,
@PathVariable Long warehouseId) {
boolean hasAccess = warehouseAccessService.hasAccess(userId, warehouseId);
return ResponseEntity.ok(hasAccess);
}

@GetMapping("/count")
@Operation(summary = "Get total number of warehouse accesses")
public ResponseEntity<Long> getTotalWarehouseAccesses() {
long count = warehouseAccessService.getTotalWarehouseAccesses();
return ResponseEntity.ok(count);
}

@GetMapping("/count/warehouse/{warehouseId}/active")
@Operation(summary = "Count active accesses for warehouse")
public ResponseEntity<Long> countActiveAccessesForWarehouse(@PathVariable Long warehouseId) {
long count = warehouseAccessService.countActiveAccessesForWarehouse(warehouseId);
return ResponseEntity.ok(count);
}

@GetMapping("/status/{status}")
@Operation(summary = "Get warehouse accesses by status")
public ResponseEntity<List<WarehouseAccess>> getWarehouseAccessesByStatus(@PathVariable WarehouseAccessStatus status) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getWarehouseAccessesByStatus(status);
return ResponseEntity.ok(warehouseAccesses);
}

@GetMapping("/warehouse/{warehouseId}/status/{status}")
@Operation(summary = "Get warehouse accesses by warehouse and status")
public ResponseEntity<List<WarehouseAccess>> getWarehouseAccessesByWarehouseAndStatus(
@PathVariable Long warehouseId,
@PathVariable WarehouseAccessStatus status) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getWarehouseAccessesByWarehouseAndStatus(warehouseId, status);
return ResponseEntity.ok(warehouseAccesses);
}

@PatchMapping("/{id}/status")
@Operation(summary = "Update warehouse access status", description = "Storekeeper only - Approve or reject warehouse access requests for assigned warehouses")
public ResponseEntity<WarehouseAccess> updateStatus(
@PathVariable Long id,
@RequestParam WarehouseAccessStatus status,
HttpServletRequest request) {
Long currentUserId = getCurrentUserId(request);
WarehouseAccess updated = warehouseAccessService.updateStatus(id, status, currentUserId);
return ResponseEntity.ok(updated);
}

private Long getCurrentUserId(HttpServletRequest request) {
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
User user = userService.getUserByEmail(email);
if (user == null || user.getId() == null) {
log.error("User not found or has no ID for email: {}", email);
throw new RuntimeException("User not found with email: " + email);
}

log.debug("Successfully got userId: {} for email: {}", user.getId(), email);
return user.getId();
} catch (NumberFormatException e) {
log.error("NumberFormatException while getting userId: {}", e.getMessage());
throw new RuntimeException("Invalid user ID format: " + e.getMessage(), e);
} catch (Exception e) {
log.error("Failed to get user ID from request: {}", e.getMessage(), e);
throw new RuntimeException("Failed to get user ID from request: " + e.getMessage(), e);
}
}

@GetMapping("/assign-storekeeper/check")
@Operation(summary = "Check if storekeeper assignment requires confirmation", description = "Admin only - Check if storekeeper already has a warehouse assignment")
public ResponseEntity<com.raf.dto.StorekeeperAssignmentResponse> checkStorekeeperAssignment(
@RequestParam Long storekeeperId,
@RequestParam Long warehouseId) {
com.raf.dto.StorekeeperAssignmentResponse response = warehouseAccessService.checkStorekeeperAssignment(
storekeeperId, warehouseId);
return ResponseEntity.ok(response);
}

@GetMapping("/verify-storekeeper-assignment/{storekeeperId}/{warehouseId}")
@Operation(summary = "Verify storekeeper assignment to warehouse", description = "Check if storekeeper has proper MANAGER-level active access to warehouse")
public ResponseEntity<Map<String, Object>> verifyStorekeeperAssignment(
@PathVariable Long storekeeperId,
@PathVariable Long warehouseId) {
return ResponseEntity.ok(warehouseAccessService.verifyStorekeeperAssignment(storekeeperId, warehouseId));
}

@PostMapping("/assign-storekeeper")
@Operation(summary = "Assign storekeeper to warehouse", description = "Admin only - Assign a storekeeper to their preferred warehouse. Use /check endpoint first to see if confirmation is needed.")
public ResponseEntity<WarehouseAccess> assignStorekeeperToWarehouse(
@Valid @RequestBody com.raf.dto.StorekeeperAssignmentRequest request) {
WarehouseAccess access = warehouseAccessService.assignStorekeeperToWarehouse(
request.getStorekeeperId(), request.getWarehouseId());
return new ResponseEntity<>(access, HttpStatus.CREATED);
}

@PostMapping("/upload-image")
@Operation(summary = "Upload crop image for warehouse access request", description = "Upload an image of the crop for warehouse access request")
public ResponseEntity<Map<String, String>> uploadCropImage(
@RequestParam("file") MultipartFile file,
HttpServletRequest httpRequest) {
try {
log.info("Received image upload request");


if (file == null || file.isEmpty()) {
log.warn("File is null or empty");
Map<String, String> errorResponse = new HashMap<>();
errorResponse.put("error", "File is required");
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
}

log.debug("File received: name={}, size={}, contentType={}",
file.getOriginalFilename(), file.getSize(), file.getContentType());

Long userId;
try {
userId = getCurrentUserId(httpRequest);
if (userId == null) {
log.error("getCurrentUserId returned null");
Map<String, String> errorResponse = new HashMap<>();
errorResponse.put("error", "User authentication failed. Please log in again.");
return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
}
log.info("Successfully extracted userId: {}", userId);
} catch (Exception e) {
log.error("Error getting user ID: {}", e.getMessage(), e);
Map<String, String> errorResponse = new HashMap<>();
errorResponse.put("error", "Authentication error: " + e.getMessage());
return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
}

log.info("Storing warehouse access image for user: {}", userId);
String imageUrl = fileStorageService.storeWarehouseAccessImage(file, userId);
log.info("Image stored successfully. URL: {}", imageUrl);

Map<String, String> response = new HashMap<>();
response.put("imageUrl", imageUrl);
response.put("message", "Image uploaded successfully");

return ResponseEntity.ok(response);
} catch (IllegalArgumentException e) {
log.error("Validation error uploading crop image: {}", e.getMessage(), e);
Map<String, String> errorResponse = new HashMap<>();
errorResponse.put("error", e.getMessage());
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
} catch (RuntimeException e) {
log.error("Runtime error uploading crop image: {}", e.getMessage(), e);
Map<String, String> errorResponse = new HashMap<>();
String errorMsg = e.getMessage();
if (errorMsg == null || errorMsg.isEmpty()) {
errorMsg = "Failed to upload image. Please try again.";
}
errorResponse.put("error", errorMsg);
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
} catch (Exception e) {
log.error("Unexpected error uploading crop image: {}", e.getMessage(), e);
Map<String, String> errorResponse = new HashMap<>();
errorResponse.put("error", "An unexpected error occurred: " + (e.getMessage() != null ? e.getMessage() : "Please try again."));
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
}

