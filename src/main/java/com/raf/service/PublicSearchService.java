package com.raf.service;

import com.raf.entity.CropType;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.Inventory;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicSearchService {

private final CropTypeRepository cropTypeRepository;
private final StorageWarehouseRepository warehouseRepository;
private final InventoryRepository inventoryRepository;


public Map<String, Object> search(String query, int page, int size) {
log.info("Public search query: '{}' (page: {}, size: {})", query, page, size);

String searchTerm = query != null ? query.trim().toLowerCase() : "";
if (searchTerm.isEmpty()) {
return createEmptyResults();
}

Map<String, Object> results = new HashMap<>();
List<Map<String, Object>> allResults = new ArrayList<>();



List<StorageWarehouse> warehouses = warehouseRepository.findAll();
List<Map<String, Object>> warehouseResults = warehouses.stream()
.filter(warehouse -> warehouse.getStatus() == com.raf.enums.WarehouseStatus.ACTIVE)
.filter(warehouse -> matchesWarehouse(warehouse, searchTerm))
.limit(10)
.map(this::mapWarehouseToResult)
.collect(Collectors.toList());
allResults.addAll(warehouseResults);


int start = page * size;
int end = Math.min(start + size, allResults.size());
List<Map<String, Object>> paginatedResults = start < allResults.size()
? allResults.subList(start, end)
: new ArrayList<>();

results.put("content", paginatedResults);
results.put("totalElements", allResults.size());
results.put("totalPages", (int) Math.ceil((double) allResults.size() / size));
results.put("page", page);
results.put("size", size);
results.put("hasNext", end < allResults.size());
results.put("hasPrevious", page > 0);

log.info("Public search found {} total results, returning {} results for page {}",
allResults.size(), paginatedResults.size(), page);

return results;
}

private boolean matchesCropType(CropType crop, String searchTerm) {
return (crop.getCropName() != null && crop.getCropName().toLowerCase().contains(searchTerm)) ||
(crop.getCategory() != null && crop.getCategory().toString().toLowerCase().contains(searchTerm)) ||
(crop.getDescription() != null && crop.getDescription().toLowerCase().contains(searchTerm)) ||
(crop.getCropCode() != null && crop.getCropCode().toLowerCase().contains(searchTerm));
}

private boolean matchesWarehouse(StorageWarehouse warehouse, String searchTerm) {
return (warehouse.getWarehouseName() != null && warehouse.getWarehouseName().toLowerCase().contains(searchTerm)) ||
(warehouse.getWarehouseCode() != null && warehouse.getWarehouseCode().toLowerCase().contains(searchTerm)) ||
(warehouse.getWarehouseType() != null && warehouse.getWarehouseType().toString().toLowerCase().contains(searchTerm)) ||
(warehouse.getLocation() != null && warehouse.getLocation().getName() != null &&
warehouse.getLocation().getName().toLowerCase().contains(searchTerm));
}

private boolean matchesInventory(Inventory inventory, String searchTerm) {
String searchableText = (inventory.getInventoryCode() != null ? inventory.getInventoryCode() : "") +
(inventory.getCropType() != null && inventory.getCropType().getCropName() != null
? " " + inventory.getCropType().getCropName() : "") +
(inventory.getQualityGrade() != null ? " " + inventory.getQualityGrade() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private Map<String, Object> mapCropTypeToResult(CropType crop) {
Map<String, Object> result = new HashMap<>();
result.put("id", crop.getId());
result.put("type", "crop_type");
result.put("title", crop.getCropName());
result.put("code", crop.getCropCode());
result.put("category", crop.getCategory() != null ? crop.getCategory().toString() : "");
result.put("description", crop.getDescription() != null ? crop.getDescription() : "");
result.put("pricePerKg", crop.getPricePerKg());
result.put("imageUrl", crop.getImageUrl());
return result;
}

private Map<String, Object> mapWarehouseToResult(StorageWarehouse warehouse) {
Map<String, Object> result = new HashMap<>();
result.put("id", warehouse.getId());
result.put("type", "warehouse");
result.put("title", warehouse.getWarehouseName());
result.put("code", warehouse.getWarehouseCode());
result.put("warehouseType", warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().toString() : "");
result.put("location", warehouse.getLocation() != null && warehouse.getLocation().getName() != null
? warehouse.getLocation().getName() : "");
result.put("status", warehouse.getStatus() != null ? warehouse.getStatus().toString() : "");
return result;
}

private Map<String, Object> mapInventoryToResult(Inventory inventory) {
Map<String, Object> result = new HashMap<>();
result.put("id", inventory.getId());
result.put("type", "inventory");
result.put("title", inventory.getCropType() != null ? inventory.getCropType().getCropName() : "Inventory");
result.put("code", inventory.getInventoryCode());
result.put("quantity", inventory.getRemainingQuantityKg());
result.put("qualityGrade", inventory.getQualityGrade());
result.put("cropType", inventory.getCropType());
result.put("imageUrl", inventory.getCropImageUrl());
return result;
}

private Map<String, Object> createEmptyResults() {
Map<String, Object> results = new HashMap<>();
results.put("content", new ArrayList<>());
results.put("totalElements", 0);
results.put("totalPages", 0);
results.put("page", 0);
results.put("size", 10);
results.put("hasNext", false);
results.put("hasPrevious", false);
return results;
}
}

