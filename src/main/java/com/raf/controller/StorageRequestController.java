package com.raf.controller;

import com.raf.dto.StorageRequestDto;
import com.raf.entity.Inventory;
import com.raf.entity.WarehouseAccess;
import com.raf.service.StorageRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/storage-requests")
@RequiredArgsConstructor
@Tag(name = "Storage Requests", description = "Storage request management APIs")
public class StorageRequestController {

private final StorageRequestService storageRequestService;

@PostMapping("/submit")
@Operation(summary = "Submit storage request", description = "Farmer submits a request for storage space")
public ResponseEntity<WarehouseAccess> submitStorageRequest(
@RequestHeader("Authorization") String authHeader,
@Valid @RequestBody StorageRequestDto request) {
String token = authHeader.substring(7);
WarehouseAccess access = storageRequestService.submitStorageRequest(token, request);
return new ResponseEntity<>(access, HttpStatus.CREATED);
}

@GetMapping("/my-requests")
@Operation(summary = "Get my storage requests", description = "Get all storage requests for the logged-in farmer")
public ResponseEntity<List<WarehouseAccess>> getMyStorageRequests(
@RequestHeader("Authorization") String authHeader) {
String token = authHeader.substring(7);
List<WarehouseAccess> requests = storageRequestService.getFarmerStorageRequests(token);
return ResponseEntity.ok(requests);
}

@GetMapping("/pending")
@Operation(summary = "Get pending storage requests", description = "Admin only - Get all pending storage requests for review")
public ResponseEntity<List<WarehouseAccess>> getPendingStorageRequests() {
List<WarehouseAccess> requests = storageRequestService.getPendingStorageRequests();
return ResponseEntity.ok(requests);
}

@PostMapping("/{requestId}/approve")
@Operation(summary = "Approve storage request", description = "Admin only - Approve a storage request and create inventory")
public ResponseEntity<Inventory> approveStorageRequest(
@PathVariable Long requestId,
@RequestParam Long storekeeperId) {
Inventory inventory = storageRequestService.approveStorageRequest(requestId, storekeeperId);
return ResponseEntity.ok(inventory);
}

@PostMapping("/{requestId}/reject")
@Operation(summary = "Reject storage request", description = "Admin only - Reject a storage request")
public ResponseEntity<Map<String, String>> rejectStorageRequest(
@PathVariable Long requestId,
@RequestParam(required = false, defaultValue = "Request rejected by admin") String reason) {
storageRequestService.rejectStorageRequest(requestId, reason);
Map<String, String> response = new HashMap<>();
response.put("message", "Storage request rejected successfully");
return ResponseEntity.ok(response);
}
}

