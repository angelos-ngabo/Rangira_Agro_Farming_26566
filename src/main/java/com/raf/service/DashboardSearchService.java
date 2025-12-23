package com.raf.service;

import com.raf.entity.*;
import com.raf.enums.UserType;
import com.raf.repository.*;
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
public class DashboardSearchService {

private final UserRepository userRepository;
private final StorageWarehouseRepository warehouseRepository;
private final InventoryRepository inventoryRepository;
private final TransactionRepository transactionRepository;
private final CropTypeRepository cropTypeRepository;
private final EnquiryRepository enquiryRepository;
private final WarehouseAccessRepository warehouseAccessRepository;


public Map<String, Object> search(String query, Long userId, UserType userType, int page, int size) {
log.info("Dashboard search query: '{}' for user {} (type: {}) (page: {}, size: {})",
query, userId, userType, page, size);

String searchTerm = query != null ? query.trim().toLowerCase() : "";
if (searchTerm.isEmpty()) {
return createEmptyResults();
}

Map<String, Object> results = new HashMap<>();
List<Map<String, Object>> allResults = new ArrayList<>();



List<CropType> cropTypes = cropTypeRepository.findAll();
List<Map<String, Object>> cropResults = cropTypes.stream()
.filter(crop -> matchesCropType(crop, searchTerm))
.limit(10)
.map(this::mapCropTypeToResult)
.collect(Collectors.toList());
allResults.addAll(cropResults);

if (userType == UserType.ADMIN) {

allResults.addAll(searchUsers(searchTerm));
allResults.addAll(searchWarehouses(searchTerm));
allResults.addAll(searchAllInventory(searchTerm));
allResults.addAll(searchAllTransactions(searchTerm));
} else if (userType == UserType.FARMER) {

allResults.addAll(searchFarmerInventory(userId, searchTerm));
allResults.addAll(searchFarmerTransactions(userId, searchTerm));
allResults.addAll(searchFarmerEnquiries(userId, searchTerm));
} else if (userType == UserType.BUYER) {

allResults.addAll(searchBuyerTransactions(userId, searchTerm));
allResults.addAll(searchBuyerEnquiries(userId, searchTerm));
allResults.addAll(searchAvailableInventory(searchTerm));
} else if (userType == UserType.STOREKEEPER) {

allResults.addAll(searchStorekeeperWarehouses(userId, searchTerm));
allResults.addAll(searchStorekeeperInventory(userId, searchTerm));
}


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

log.info("Dashboard search found {} total results, returning {} results for page {}",
allResults.size(), paginatedResults.size(), page);

return results;
}


private List<Map<String, Object>> searchUsers(String searchTerm) {
return userRepository.findAll().stream()
.filter(user -> matchesUser(user, searchTerm))
.limit(10)
.map(this::mapUserToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchWarehouses(String searchTerm) {
return warehouseRepository.findAll().stream()
.filter(warehouse -> matchesWarehouse(warehouse, searchTerm))
.limit(10)
.map(this::mapWarehouseToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllInventory(String searchTerm) {
return inventoryRepository.findAll().stream()
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(10)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllTransactions(String searchTerm) {
return transactionRepository.findAll().stream()
.filter(transaction -> matchesTransaction(transaction, searchTerm))
.limit(10)
.map(this::mapTransactionToResult)
.collect(Collectors.toList());
}


private List<Map<String, Object>> searchFarmerInventory(Long farmerId, String searchTerm) {
return inventoryRepository.findByFarmerId(farmerId).stream()
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(10)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerTransactions(Long farmerId, String searchTerm) {
return transactionRepository.findBySellerId(farmerId).stream()
.filter(transaction -> matchesTransaction(transaction, searchTerm))
.limit(10)
.map(this::mapTransactionToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerEnquiries(Long farmerId, String searchTerm) {
return enquiryRepository.findByFarmerId(farmerId).stream()
.filter(enquiry -> matchesEnquiry(enquiry, searchTerm))
.limit(10)
.map(this::mapEnquiryToResult)
.collect(Collectors.toList());
}


private List<Map<String, Object>> searchBuyerTransactions(Long buyerId, String searchTerm) {
return transactionRepository.findByBuyerId(buyerId).stream()
.filter(transaction -> matchesTransaction(transaction, searchTerm))
.limit(10)
.map(this::mapTransactionToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerEnquiries(Long buyerId, String searchTerm) {
return enquiryRepository.findByBuyerId(buyerId).stream()
.filter(enquiry -> matchesEnquiry(enquiry, searchTerm))
.limit(10)
.map(this::mapEnquiryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAvailableInventory(String searchTerm) {
return inventoryRepository.findByStatus(com.raf.enums.InventoryStatus.STORED).stream()
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(10)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}


private List<Map<String, Object>> searchStorekeeperWarehouses(Long storekeeperId, String searchTerm) {
List<Long> warehouseIds = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true).stream()
.map(access -> access.getWarehouse().getId())
.collect(Collectors.toList());

return warehouseRepository.findAllById(warehouseIds != null ? warehouseIds : new ArrayList<>()).stream()
.filter(warehouse -> matchesWarehouse(warehouse, searchTerm))
.limit(10)
.map(this::mapWarehouseToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchStorekeeperInventory(Long storekeeperId, String searchTerm) {
List<Long> warehouseIds = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true).stream()
.map(access -> access.getWarehouse().getId())
.collect(Collectors.toList());

return inventoryRepository.findAll().stream()
.filter(inventory -> inventory.getWarehouse() != null &&
warehouseIds.contains(inventory.getWarehouse().getId()))
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(10)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
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

private boolean matchesTransaction(Transaction transaction, String searchTerm) {
return (transaction.getTransactionCode() != null && transaction.getTransactionCode().toLowerCase().contains(searchTerm)) ||
(transaction.getInventory() != null && transaction.getInventory().getCropType() != null &&
transaction.getInventory().getCropType().getCropName() != null &&
transaction.getInventory().getCropType().getCropName().toLowerCase().contains(searchTerm));
}

private boolean matchesEnquiry(Enquiry enquiry, String searchTerm) {
return (enquiry.getEnquiryCode() != null && enquiry.getEnquiryCode().toLowerCase().contains(searchTerm)) ||
(enquiry.getInventory() != null && enquiry.getInventory().getCropType() != null &&
enquiry.getInventory().getCropType().getCropName() != null &&
enquiry.getInventory().getCropType().getCropName().toLowerCase().contains(searchTerm));
}

private boolean matchesUser(User user, String searchTerm) {
return (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchTerm)) ||
(user.getFirstName() != null && user.getFirstName().toLowerCase().contains(searchTerm)) ||
(user.getLastName() != null && user.getLastName().toLowerCase().contains(searchTerm)) ||
(user.getUserCode() != null && user.getUserCode().toLowerCase().contains(searchTerm)) ||
(user.getPhoneNumber() != null && user.getPhoneNumber().contains(searchTerm));
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

private Map<String, Object> mapTransactionToResult(Transaction transaction) {
Map<String, Object> result = new HashMap<>();
result.put("id", transaction.getId());
result.put("type", "transaction");
result.put("title", transaction.getInventory() != null && transaction.getInventory().getCropType() != null
? transaction.getInventory().getCropType().getCropName() : "Transaction");
result.put("code", transaction.getTransactionCode());
result.put("status", transaction.getPaymentStatus() != null ? transaction.getPaymentStatus().toString() : "");
result.put("totalAmount", transaction.getTotalAmount());
result.put("transactionDate", transaction.getTransactionDate());
return result;
}

private Map<String, Object> mapEnquiryToResult(Enquiry enquiry) {
Map<String, Object> result = new HashMap<>();
result.put("id", enquiry.getId());
result.put("type", "enquiry");
result.put("title", enquiry.getInventory() != null && enquiry.getInventory().getCropType() != null
? enquiry.getInventory().getCropType().getCropName() : "Enquiry");
result.put("code", enquiry.getEnquiryCode());
result.put("status", enquiry.getStatus() != null ? enquiry.getStatus().toString() : "");
result.put("proposedQuantityKg", enquiry.getProposedQuantityKg());
result.put("proposedPricePerKg", enquiry.getProposedPricePerKg());
return result;
}

private Map<String, Object> mapUserToResult(User user) {
Map<String, Object> result = new HashMap<>();
result.put("id", user.getId());
result.put("type", "user");
result.put("title", (user.getFirstName() != null ? user.getFirstName() : "") +
" " + (user.getLastName() != null ? user.getLastName() : ""));
result.put("code", user.getUserCode());
result.put("email", user.getEmail());
result.put("userType", user.getUserType() != null ? user.getUserType().toString() : "");
result.put("phoneNumber", user.getPhoneNumber());
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

