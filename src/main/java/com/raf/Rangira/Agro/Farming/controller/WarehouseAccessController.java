package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.entity.WarehouseAccess;
import com.raf.Rangira.Agro.Farming.enums.AccessLevel;
import com.raf.Rangira.Agro.Farming.service.WarehouseAccessService;
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

import java.util.List;

/**
 * WarehouseAccess REST Controller
 */
@RestController
@RequestMapping("/api/warehouse-accesses")
@RequiredArgsConstructor
@Tag(name = "WarehouseAccess", description = "Warehouse access management APIs")
public class WarehouseAccessController {
    
    private final WarehouseAccessService warehouseAccessService;
    
    @PostMapping
    @Operation(summary = "Create a new warehouse access")
    public ResponseEntity<WarehouseAccess> createWarehouseAccess(@Valid @RequestBody WarehouseAccess warehouseAccess) {
        WarehouseAccess createdWarehouseAccess = warehouseAccessService.createWarehouseAccess(warehouseAccess);
        return new ResponseEntity<>(createdWarehouseAccess, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all warehouse accesses")
    public ResponseEntity<List<WarehouseAccess>> getAllWarehouseAccesses() {
        List<WarehouseAccess> warehouseAccesses = warehouseAccessService.getAllWarehouseAccesses();
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
            @Valid @RequestBody WarehouseAccess warehouseAccess) {
        WarehouseAccess updatedWarehouseAccess = warehouseAccessService.updateWarehouseAccess(id, warehouseAccess);
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
}

