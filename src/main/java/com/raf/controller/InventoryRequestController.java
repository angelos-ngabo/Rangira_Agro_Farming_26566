package com.raf.controller;

import com.raf.dto.InventoryRequestCreateDto;
import com.raf.dto.InventoryRequestResponseDto;
import com.raf.entity.InventoryRequest;
import com.raf.service.InventoryRequestService;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Request", description = "Inventory update/withdrawal request management APIs")
public class InventoryRequestController {

private final InventoryRequestService inventoryRequestService;
private final JwtUtil jwtUtil;
private final UserService userService;

@PostMapping
@Operation(summary = "Create inventory request (Farmer only)", description = "Farmer submits a request to update or withdraw inventory")
public ResponseEntity<InventoryRequest> createRequest(
@Valid @RequestBody InventoryRequestCreateDto dto,
HttpServletRequest request) {
Long farmerId = getCurrentUserId(request);
log.info("🌐 API Request: POST /api/inventory-requests - Farmer {} creating request", farmerId);
InventoryRequest createdRequest = inventoryRequestService.createRequest(farmerId, dto);
log.info("✅ API Response: Successfully created inventory request {}", createdRequest.getRequestCode());
return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
}

@GetMapping("/my-requests")
@Operation(summary = "Get farmer's inventory requests", description = "Get all inventory requests submitted by the current farmer")
public ResponseEntity<List<InventoryRequest>> getMyRequests(HttpServletRequest request) {
Long farmerId = getCurrentUserId(request);
log.info("🌐 API Request: GET /api/inventory-requests/my-requests - Farmer {} fetching requests", farmerId);
List<InventoryRequest> requests = inventoryRequestService.getRequestsByFarmer(farmerId);
log.info("✅ API Response: Returning {} requests to farmer {}", requests.size(), farmerId);
return ResponseEntity.ok(requests);
}

@GetMapping("/storekeeper/pending")
@Operation(summary = "Get pending requests for storekeeper", description = "Get all pending inventory requests for the current storekeeper")
public ResponseEntity<List<InventoryRequest>> getPendingRequests(HttpServletRequest request) {
Long storekeeperId = getCurrentUserId(request);
log.info("🌐 API Request: GET /api/inventory-requests/storekeeper/pending - Storekeeper {} fetching pending requests", storekeeperId);
List<InventoryRequest> requests = inventoryRequestService.getPendingRequestsByStorekeeper(storekeeperId);
log.info("✅ API Response: Returning {} pending requests to storekeeper {}", requests.size(), storekeeperId);
return ResponseEntity.ok(requests);
}

@GetMapping("/storekeeper/all")
@Operation(summary = "Get all requests for storekeeper", description = "Get all inventory requests (all statuses) for the current storekeeper")
public ResponseEntity<List<InventoryRequest>> getAllRequests(HttpServletRequest request) {
Long storekeeperId = getCurrentUserId(request);
log.info("🌐 API Request: GET /api/inventory-requests/storekeeper/all - Storekeeper {} fetching all requests", storekeeperId);
List<InventoryRequest> requests = inventoryRequestService.getRequestsByStorekeeper(storekeeperId);
log.info("✅ API Response: Returning {} requests to storekeeper {}", requests.size(), storekeeperId);
return ResponseEntity.ok(requests);
}

@PostMapping("/{id}/respond")
@Operation(summary = "Respond to inventory request (Storekeeper only)", description = "Storekeeper approves or rejects an inventory request")
public ResponseEntity<InventoryRequest> respondToRequest(
@PathVariable Long id,
@Valid @RequestBody InventoryRequestResponseDto dto,
HttpServletRequest request) {
Long storekeeperId = getCurrentUserId(request);
log.info("🌐 API Request: POST /api/inventory-requests/{}/respond - Storekeeper {} responding", id, storekeeperId);
InventoryRequest updatedRequest = inventoryRequestService.respondToRequest(id, storekeeperId, dto);
log.info("✅ API Response: Storekeeper {} {} request {}", storekeeperId, dto.getApprove() ? "approved" : "rejected", id);
return ResponseEntity.ok(updatedRequest);
}

@GetMapping("/{id}")
@Operation(summary = "Get inventory request by ID")
public ResponseEntity<InventoryRequest> getRequestById(@PathVariable Long id) {
InventoryRequest request = inventoryRequestService.getRequestById(id);
return ResponseEntity.ok(request);
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
}

