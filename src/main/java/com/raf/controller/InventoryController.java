package com.raf.controller;

import com.raf.dto.InventoryRequest;
import com.raf.entity.Inventory;
import com.raf.enums.InventoryStatus;
import com.raf.service.InventoryService;
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
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {
    private final InventoryService inventoryService;
    
    @PostMapping
    @Operation(summary = "Create new inventory - use InventoryRequest with IDs")
    public ResponseEntity<Inventory> createInventory(@Valid @RequestBody InventoryRequest request) {
        Inventory createdInventory = inventoryService.createInventoryFromRequest(request);
        return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all inventories")
    public ResponseEntity<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryService.getAllInventories();
        return ResponseEntity.ok(inventories);
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get inventories with pagination")
    public ResponseEntity<Page<Inventory>> getInventoriesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("storageDate").descending());
        Page<Inventory> inventories = inventoryService.getInventoriesPaginated(pageRequest);
        return ResponseEntity.ok(inventories);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get all available (stored) inventories")
    public ResponseEntity<List<Inventory>> getAvailableInventories() {
        List<Inventory> inventories = inventoryService.getInventoriesByStatus(InventoryStatus.STORED);
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
    public ResponseEntity<List<Inventory>> getInventoriesByWarehouse(@PathVariable Long warehouseId) {
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
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
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
}

