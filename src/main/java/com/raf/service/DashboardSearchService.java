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
private final MessageRepository messageRepository;
private final RatingRepository ratingRepository;
private final LocationRepository locationRepository;
private final NotificationRepository notificationRepository;
private final InventoryRequestRepository inventoryRequestRepository;
private final WithdrawalRepository withdrawalRepository;

public Map<String, Object> search(String query, Long userId, UserType userType, int page, int size) {
log.info("Dashboard search query: '{}' for user {} (type: {}) (page: {}, size: {})",
query, userId, userType, page, size);

try {
String searchTerm = query != null ? query.trim().toLowerCase() : "";
if (searchTerm.isEmpty()) {
return createEmptyResults();
}

Map<String, Object> results = new HashMap<>();
List<Map<String, Object>> allResults = new ArrayList<>();

try {
allResults.addAll(searchCropTypes(searchTerm));
allResults.addAll(searchLocations(searchTerm));
} catch (Exception e) {
log.warn("Error searching crop types or locations: {}", e.getMessage());
}

if (userType == UserType.ADMIN) {
try {
allResults.addAll(searchUsers(searchTerm));
allResults.addAll(searchWarehouses(searchTerm));
allResults.addAll(searchAllInventory(searchTerm));
allResults.addAll(searchAllTransactions(searchTerm));
allResults.addAll(searchAllEnquiries(searchTerm));
allResults.addAll(searchAllMessages(searchTerm));
allResults.addAll(searchAllRatings(searchTerm));
allResults.addAll(searchAllNotifications(searchTerm));
allResults.addAll(searchAllInventoryRequests(searchTerm));
allResults.addAll(searchAllWarehouseAccesses(searchTerm));
allResults.addAll(searchAllWithdrawals(searchTerm));
} catch (Exception e) {
log.warn("Error in admin search: {}", e.getMessage());
}
} else if (userType == UserType.FARMER) {
try {
allResults.addAll(searchFarmerInventory(userId, searchTerm));
allResults.addAll(searchFarmerTransactions(userId, searchTerm));
allResults.addAll(searchFarmerEnquiries(userId, searchTerm));
allResults.addAll(searchFarmerMessages(userId, searchTerm));
allResults.addAll(searchFarmerRatings(userId, searchTerm));
allResults.addAll(searchFarmerNotifications(userId, searchTerm));
allResults.addAll(searchFarmerInventoryRequests(userId, searchTerm));
allResults.addAll(searchFarmerWarehouseAccesses(userId, searchTerm));
allResults.addAll(searchFarmerWithdrawals(userId, searchTerm));
} catch (Exception e) {
log.warn("Error in farmer search: {}", e.getMessage());
}
} else if (userType == UserType.BUYER) {
try {
allResults.addAll(searchBuyerTransactions(userId, searchTerm));
allResults.addAll(searchBuyerEnquiries(userId, searchTerm));
allResults.addAll(searchAvailableInventory(searchTerm));
allResults.addAll(searchBuyerMessages(userId, searchTerm));
allResults.addAll(searchBuyerRatings(userId, searchTerm));
allResults.addAll(searchBuyerNotifications(userId, searchTerm));
allResults.addAll(searchBuyerWithdrawals(userId, searchTerm));
} catch (Exception e) {
log.warn("Error in buyer search: {}", e.getMessage());
}
} else if (userType == UserType.STOREKEEPER) {
try {
allResults.addAll(searchStorekeeperWarehouses(userId, searchTerm));
allResults.addAll(searchStorekeeperInventory(userId, searchTerm));
allResults.addAll(searchStorekeeperMessages(userId, searchTerm));
allResults.addAll(searchStorekeeperNotifications(userId, searchTerm));
allResults.addAll(searchStorekeeperInventoryRequests(userId, searchTerm));
allResults.addAll(searchStorekeeperWarehouseAccesses(userId, searchTerm));
} catch (Exception e) {
log.warn("Error in storekeeper search: {}", e.getMessage());
}
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
} catch (Exception e) {
log.error("Unexpected error in dashboard search: {}", e.getMessage(), e);
return createEmptyResults();
}
}

private List<Map<String, Object>> searchCropTypes(String searchTerm) {
return cropTypeRepository.findAll().stream()
.filter(crop -> matchesCropType(crop, searchTerm))
.limit(50)
.map(this::mapCropTypeToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchLocations(String searchTerm) {
return locationRepository.findAll().stream()
.filter(location -> matchesLocation(location, searchTerm))
.limit(50)
.map(this::mapLocationToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchUsers(String searchTerm) {
return userRepository.findAll().stream()
.filter(user -> matchesUser(user, searchTerm))
.limit(50)
.map(this::mapUserToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchWarehouses(String searchTerm) {
return warehouseRepository.findAll().stream()
.filter(warehouse -> matchesWarehouse(warehouse, searchTerm))
.limit(50)
.map(this::mapWarehouseToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllInventory(String searchTerm) {
return inventoryRepository.findAll().stream()
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(50)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllTransactions(String searchTerm) {
return transactionRepository.findAll().stream()
.filter(transaction -> matchesTransaction(transaction, searchTerm))
.limit(50)
.map(this::mapTransactionToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllEnquiries(String searchTerm) {
return enquiryRepository.findAll().stream()
.filter(enquiry -> matchesEnquiry(enquiry, searchTerm))
.limit(50)
.map(this::mapEnquiryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllMessages(String searchTerm) {
return messageRepository.findAll().stream()
.filter(message -> matchesMessage(message, searchTerm))
.limit(50)
.map(this::mapMessageToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllRatings(String searchTerm) {
return ratingRepository.findAll().stream()
.filter(rating -> matchesRating(rating, searchTerm))
.limit(50)
.map(this::mapRatingToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllNotifications(String searchTerm) {
return notificationRepository.findAll().stream()
.filter(notification -> matchesNotification(notification, searchTerm))
.limit(50)
.map(this::mapNotificationToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllInventoryRequests(String searchTerm) {
return inventoryRequestRepository.findAll().stream()
.filter(request -> matchesInventoryRequest(request, searchTerm))
.limit(50)
.map(this::mapInventoryRequestToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllWarehouseAccesses(String searchTerm) {
return warehouseAccessRepository.findAll().stream()
.filter(access -> matchesWarehouseAccess(access, searchTerm))
.limit(50)
.map(this::mapWarehouseAccessToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAllWithdrawals(String searchTerm) {
return withdrawalRepository.findAll().stream()
.filter(withdrawal -> matchesWithdrawal(withdrawal, searchTerm))
.limit(50)
.map(this::mapWithdrawalToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerInventory(Long farmerId, String searchTerm) {
return inventoryRepository.findByFarmerId(farmerId).stream()
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(50)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerTransactions(Long farmerId, String searchTerm) {
return transactionRepository.findBySellerId(farmerId).stream()
.filter(transaction -> matchesTransaction(transaction, searchTerm))
.limit(50)
.map(this::mapTransactionToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerEnquiries(Long farmerId, String searchTerm) {
return enquiryRepository.findByFarmerId(farmerId).stream()
.filter(enquiry -> matchesEnquiry(enquiry, searchTerm))
.limit(50)
.map(this::mapEnquiryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerMessages(Long farmerId, String searchTerm) {
return messageRepository.findAll().stream()
.filter(message -> (message.getSender() != null && message.getSender().getId().equals(farmerId)) ||
(message.getReceiver() != null && message.getReceiver().getId().equals(farmerId)))
.filter(message -> matchesMessage(message, searchTerm))
.limit(50)
.map(this::mapMessageToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerRatings(Long farmerId, String searchTerm) {
return ratingRepository.findAll().stream()
.filter(rating -> (rating.getRater() != null && rating.getRater().getId().equals(farmerId)) ||
(rating.getRatedUser() != null && rating.getRatedUser().getId().equals(farmerId)))
.filter(rating -> matchesRating(rating, searchTerm))
.limit(50)
.map(this::mapRatingToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerNotifications(Long farmerId, String searchTerm) {
return notificationRepository.findByUserId(farmerId).stream()
.filter(notification -> matchesNotification(notification, searchTerm))
.limit(50)
.map(this::mapNotificationToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerInventoryRequests(Long farmerId, String searchTerm) {
return inventoryRequestRepository.findByFarmerId(farmerId).stream()
.filter(request -> matchesInventoryRequest(request, searchTerm))
.limit(50)
.map(this::mapInventoryRequestToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerWarehouseAccesses(Long farmerId, String searchTerm) {
return warehouseAccessRepository.findByUserId(farmerId).stream()
.filter(access -> matchesWarehouseAccess(access, searchTerm))
.limit(50)
.map(this::mapWarehouseAccessToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchFarmerWithdrawals(Long farmerId, String searchTerm) {
return withdrawalRepository.findByUserId(farmerId).stream()
.filter(withdrawal -> matchesWithdrawal(withdrawal, searchTerm))
.limit(50)
.map(this::mapWithdrawalToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerTransactions(Long buyerId, String searchTerm) {
return transactionRepository.findByBuyerId(buyerId).stream()
.filter(transaction -> matchesTransaction(transaction, searchTerm))
.limit(50)
.map(this::mapTransactionToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerEnquiries(Long buyerId, String searchTerm) {
return enquiryRepository.findByBuyerId(buyerId).stream()
.filter(enquiry -> matchesEnquiry(enquiry, searchTerm))
.limit(50)
.map(this::mapEnquiryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchAvailableInventory(String searchTerm) {
return inventoryRepository.findByStatus(com.raf.enums.InventoryStatus.STORED).stream()
.filter(inventory -> matchesInventory(inventory, searchTerm))
.limit(50)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerMessages(Long buyerId, String searchTerm) {
return messageRepository.findAll().stream()
.filter(message -> (message.getSender() != null && message.getSender().getId().equals(buyerId)) ||
(message.getReceiver() != null && message.getReceiver().getId().equals(buyerId)))
.filter(message -> matchesMessage(message, searchTerm))
.limit(50)
.map(this::mapMessageToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerRatings(Long buyerId, String searchTerm) {
return ratingRepository.findAll().stream()
.filter(rating -> (rating.getRater() != null && rating.getRater().getId().equals(buyerId)) ||
(rating.getRatedUser() != null && rating.getRatedUser().getId().equals(buyerId)))
.filter(rating -> matchesRating(rating, searchTerm))
.limit(50)
.map(this::mapRatingToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerNotifications(Long buyerId, String searchTerm) {
return notificationRepository.findByUserId(buyerId).stream()
.filter(notification -> matchesNotification(notification, searchTerm))
.limit(50)
.map(this::mapNotificationToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchBuyerWithdrawals(Long buyerId, String searchTerm) {
return withdrawalRepository.findByUserId(buyerId).stream()
.filter(withdrawal -> matchesWithdrawal(withdrawal, searchTerm))
.limit(50)
.map(this::mapWithdrawalToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchStorekeeperWarehouses(Long storekeeperId, String searchTerm) {
List<Long> warehouseIds = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true).stream()
.map(access -> access.getWarehouse().getId())
.collect(Collectors.toList());

return warehouseRepository.findAllById(warehouseIds != null ? warehouseIds : new ArrayList<>()).stream()
.filter(warehouse -> matchesWarehouse(warehouse, searchTerm))
.limit(50)
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
.limit(50)
.map(this::mapInventoryToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchStorekeeperMessages(Long storekeeperId, String searchTerm) {
return messageRepository.findAll().stream()
.filter(message -> (message.getSender() != null && message.getSender().getId().equals(storekeeperId)) ||
(message.getReceiver() != null && message.getReceiver().getId().equals(storekeeperId)))
.filter(message -> matchesMessage(message, searchTerm))
.limit(50)
.map(this::mapMessageToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchStorekeeperNotifications(Long storekeeperId, String searchTerm) {
return notificationRepository.findByUserId(storekeeperId).stream()
.filter(notification -> matchesNotification(notification, searchTerm))
.limit(50)
.map(this::mapNotificationToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchStorekeeperInventoryRequests(Long storekeeperId, String searchTerm) {
return inventoryRequestRepository.findByStorekeeperId(storekeeperId).stream()
.filter(request -> matchesInventoryRequest(request, searchTerm))
.limit(50)
.map(this::mapInventoryRequestToResult)
.collect(Collectors.toList());
}

private List<Map<String, Object>> searchStorekeeperWarehouseAccesses(Long storekeeperId, String searchTerm) {
return warehouseAccessRepository.findByUserId(storekeeperId).stream()
.filter(access -> matchesWarehouseAccess(access, searchTerm))
.limit(50)
.map(this::mapWarehouseAccessToResult)
.collect(Collectors.toList());
}

private boolean matchesCropType(CropType crop, String searchTerm) {
String searchableText = (crop.getCropName() != null ? crop.getCropName() : "") +
" " + (crop.getCropCode() != null ? crop.getCropCode() : "") +
" " + (crop.getCategory() != null ? crop.getCategory().toString() : "") +
" " + (crop.getDescription() != null ? crop.getDescription() : "") +
" " + (crop.getPricePerKg() != null ? crop.getPricePerKg().toString() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesLocation(Location location, String searchTerm) {
String searchableText = (location.getProvince() != null ? location.getProvince() : "") +
" " + (location.getDistrict() != null ? location.getDistrict() : "") +
" " + (location.getSector() != null ? location.getSector() : "") +
" " + (location.getCell() != null ? location.getCell() : "") +
" " + (location.getVillage() != null ? location.getVillage() : "") +
" " + (location.getCode() != null ? location.getCode() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesWarehouse(StorageWarehouse warehouse, String searchTerm) {
String searchableText = (warehouse.getWarehouseName() != null ? warehouse.getWarehouseName() : "") +
" " + (warehouse.getWarehouseCode() != null ? warehouse.getWarehouseCode() : "") +
" " + (warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().toString() : "") +
" " + (warehouse.getLocation() != null && warehouse.getLocation().getVillage() != null ? warehouse.getLocation().getVillage() : "") +
" " + (warehouse.getStatus() != null ? warehouse.getStatus().toString() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesInventory(Inventory inventory, String searchTerm) {
String searchableText = (inventory.getInventoryCode() != null ? inventory.getInventoryCode() : "") +
" " + (inventory.getCropType() != null && inventory.getCropType().getCropName() != null ? inventory.getCropType().getCropName() : "") +
" " + (inventory.getCropType() != null && inventory.getCropType().getCropCode() != null ? inventory.getCropType().getCropCode() : "") +
" " + (inventory.getQualityGrade() != null ? inventory.getQualityGrade() : "") +
" " + (inventory.getNotes() != null ? inventory.getNotes() : "") +
" " + (inventory.getStatus() != null ? inventory.getStatus().toString() : "") +
" " + (inventory.getWarehouse() != null && inventory.getWarehouse().getWarehouseName() != null ? inventory.getWarehouse().getWarehouseName() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesTransaction(Transaction transaction, String searchTerm) {
String searchableText = (transaction.getTransactionCode() != null ? transaction.getTransactionCode() : "") +
" " + (transaction.getPaymentStatus() != null ? transaction.getPaymentStatus().toString() : "") +
" " + (transaction.getDeliveryStatus() != null ? transaction.getDeliveryStatus().toString() : "") +
" " + (transaction.getTotalAmount() != null ? transaction.getTotalAmount().toString() : "") +
" " + (transaction.getInventory() != null && transaction.getInventory().getCropType() != null && transaction.getInventory().getCropType().getCropName() != null ? transaction.getInventory().getCropType().getCropName() : "") +
" " + (transaction.getBuyer() != null && transaction.getBuyer().getFirstName() != null ? transaction.getBuyer().getFirstName() : "") +
" " + (transaction.getBuyer() != null && transaction.getBuyer().getLastName() != null ? transaction.getBuyer().getLastName() : "") +
" " + (transaction.getBuyer() != null && transaction.getBuyer().getEmail() != null ? transaction.getBuyer().getEmail() : "") +
" " + (transaction.getSeller() != null && transaction.getSeller().getFirstName() != null ? transaction.getSeller().getFirstName() : "") +
" " + (transaction.getSeller() != null && transaction.getSeller().getLastName() != null ? transaction.getSeller().getLastName() : "") +
" " + (transaction.getSeller() != null && transaction.getSeller().getEmail() != null ? transaction.getSeller().getEmail() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesEnquiry(Enquiry enquiry, String searchTerm) {
String searchableText = (enquiry.getEnquiryCode() != null ? enquiry.getEnquiryCode() : "") +
" " + (enquiry.getMessage() != null ? enquiry.getMessage() : "") +
" " + (enquiry.getStatus() != null ? enquiry.getStatus().toString() : "") +
" " + (enquiry.getProposedQuantityKg() != null ? enquiry.getProposedQuantityKg().toString() : "") +
" " + (enquiry.getProposedPricePerKg() != null ? enquiry.getProposedPricePerKg().toString() : "") +
" " + (enquiry.getInventory() != null && enquiry.getInventory().getCropType() != null && enquiry.getInventory().getCropType().getCropName() != null ? enquiry.getInventory().getCropType().getCropName() : "") +
" " + (enquiry.getBuyer() != null && enquiry.getBuyer().getFirstName() != null ? enquiry.getBuyer().getFirstName() : "") +
" " + (enquiry.getBuyer() != null && enquiry.getBuyer().getLastName() != null ? enquiry.getBuyer().getLastName() : "") +
" " + (enquiry.getBuyer() != null && enquiry.getBuyer().getEmail() != null ? enquiry.getBuyer().getEmail() : "") +
" " + (enquiry.getFarmer() != null && enquiry.getFarmer().getFirstName() != null ? enquiry.getFarmer().getFirstName() : "") +
" " + (enquiry.getFarmer() != null && enquiry.getFarmer().getLastName() != null ? enquiry.getFarmer().getLastName() : "") +
" " + (enquiry.getFarmer() != null && enquiry.getFarmer().getEmail() != null ? enquiry.getFarmer().getEmail() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesMessage(Message message, String searchTerm) {
String searchableText = (message.getMessageCode() != null ? message.getMessageCode() : "") +
" " + (message.getSubject() != null ? message.getSubject() : "") +
" " + (message.getContent() != null ? message.getContent() : "") +
" " + (message.getSender() != null && message.getSender().getFirstName() != null ? message.getSender().getFirstName() : "") +
" " + (message.getSender() != null && message.getSender().getLastName() != null ? message.getSender().getLastName() : "") +
" " + (message.getSender() != null && message.getSender().getEmail() != null ? message.getSender().getEmail() : "") +
" " + (message.getReceiver() != null && message.getReceiver().getFirstName() != null ? message.getReceiver().getFirstName() : "") +
" " + (message.getReceiver() != null && message.getReceiver().getLastName() != null ? message.getReceiver().getLastName() : "") +
" " + (message.getReceiver() != null && message.getReceiver().getEmail() != null ? message.getReceiver().getEmail() : "") +
" " + (message.getRelatedInventory() != null && message.getRelatedInventory().getCropType() != null && message.getRelatedInventory().getCropType().getCropName() != null ? message.getRelatedInventory().getCropType().getCropName() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesRating(Rating rating, String searchTerm) {
String searchableText = (rating.getComment() != null ? rating.getComment() : "") +
" " + (rating.getRatingScore() != null ? rating.getRatingScore().toString() : "") +
" " + (rating.getRatingType() != null ? rating.getRatingType().toString() : "") +
" " + (rating.getRater() != null && rating.getRater().getFirstName() != null ? rating.getRater().getFirstName() : "") +
" " + (rating.getRater() != null && rating.getRater().getLastName() != null ? rating.getRater().getLastName() : "") +
" " + (rating.getRater() != null && rating.getRater().getEmail() != null ? rating.getRater().getEmail() : "") +
" " + (rating.getRatedUser() != null && rating.getRatedUser().getFirstName() != null ? rating.getRatedUser().getFirstName() : "") +
" " + (rating.getRatedUser() != null && rating.getRatedUser().getLastName() != null ? rating.getRatedUser().getLastName() : "") +
" " + (rating.getRatedUser() != null && rating.getRatedUser().getEmail() != null ? rating.getRatedUser().getEmail() : "") +
" " + (rating.getTransaction() != null && rating.getTransaction().getTransactionCode() != null ? rating.getTransaction().getTransactionCode() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesNotification(Notification notification, String searchTerm) {
String searchableText = (notification.getTitle() != null ? notification.getTitle() : "") +
" " + (notification.getMessage() != null ? notification.getMessage() : "") +
" " + (notification.getType() != null ? notification.getType().toString() : "") +
" " + (notification.getUser() != null && notification.getUser().getFirstName() != null ? notification.getUser().getFirstName() : "") +
" " + (notification.getUser() != null && notification.getUser().getLastName() != null ? notification.getUser().getLastName() : "") +
" " + (notification.getUser() != null && notification.getUser().getEmail() != null ? notification.getUser().getEmail() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesInventoryRequest(InventoryRequest request, String searchTerm) {
String searchableText = (request.getRequestCode() != null ? request.getRequestCode() : "") +
" " + (request.getNewNotes() != null ? request.getNewNotes() : "") +
" " + (request.getFarmerNotes() != null ? request.getFarmerNotes() : "") +
" " + (request.getStorekeeperResponse() != null ? request.getStorekeeperResponse() : "") +
" " + (request.getStatus() != null ? request.getStatus().toString() : "") +
" " + (request.getRequestType() != null ? request.getRequestType().toString() : "") +
" " + (request.getInventory() != null && request.getInventory().getCropType() != null && request.getInventory().getCropType().getCropName() != null ? request.getInventory().getCropType().getCropName() : "") +
" " + (request.getFarmer() != null && request.getFarmer().getFirstName() != null ? request.getFarmer().getFirstName() : "") +
" " + (request.getFarmer() != null && request.getFarmer().getLastName() != null ? request.getFarmer().getLastName() : "") +
" " + (request.getFarmer() != null && request.getFarmer().getEmail() != null ? request.getFarmer().getEmail() : "") +
" " + (request.getStorekeeper() != null && request.getStorekeeper().getFirstName() != null ? request.getStorekeeper().getFirstName() : "") +
" " + (request.getStorekeeper() != null && request.getStorekeeper().getLastName() != null ? request.getStorekeeper().getLastName() : "") +
" " + (request.getStorekeeper() != null && request.getStorekeeper().getEmail() != null ? request.getStorekeeper().getEmail() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesWarehouseAccess(WarehouseAccess access, String searchTerm) {
String searchableText = (access.getNotes() != null ? access.getNotes() : "") +
" " + (access.getStatus() != null ? access.getStatus().toString() : "") +
" " + (access.getAccessLevel() != null ? access.getAccessLevel().toString() : "") +
" " + (access.getUser() != null && access.getUser().getFirstName() != null ? access.getUser().getFirstName() : "") +
" " + (access.getUser() != null && access.getUser().getLastName() != null ? access.getUser().getLastName() : "") +
" " + (access.getUser() != null && access.getUser().getEmail() != null ? access.getUser().getEmail() : "") +
" " + (access.getWarehouse() != null && access.getWarehouse().getWarehouseName() != null ? access.getWarehouse().getWarehouseName() : "") +
" " + (access.getWarehouse() != null && access.getWarehouse().getWarehouseCode() != null ? access.getWarehouse().getWarehouseCode() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesWithdrawal(Withdrawal withdrawal, String searchTerm) {
String searchableText = (withdrawal.getWithdrawalCode() != null ? withdrawal.getWithdrawalCode() : "") +
" " + (withdrawal.getStatus() != null ? withdrawal.getStatus().toString() : "") +
" " + (withdrawal.getAmount() != null ? withdrawal.getAmount().toString() : "") +
" " + (withdrawal.getAccountNumber() != null ? withdrawal.getAccountNumber() : "") +
" " + (withdrawal.getBankName() != null ? withdrawal.getBankName() : "") +
" " + (withdrawal.getAccountName() != null ? withdrawal.getAccountName() : "") +
" " + (withdrawal.getProcessingNotes() != null ? withdrawal.getProcessingNotes() : "");
return searchableText.toLowerCase().contains(searchTerm);
}

private boolean matchesUser(User user, String searchTerm) {
String searchableText = (user.getEmail() != null ? user.getEmail() : "") +
" " + (user.getFirstName() != null ? user.getFirstName() : "") +
" " + (user.getLastName() != null ? user.getLastName() : "") +
" " + (user.getUserCode() != null ? user.getUserCode() : "") +
" " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "") +
" " + (user.getUserType() != null ? user.getUserType().toString() : "") +
" " + (user.getStatus() != null ? user.getStatus().toString() : "");
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

private Map<String, Object> mapLocationToResult(Location location) {
Map<String, Object> result = new HashMap<>();
result.put("id", location.getId());
result.put("type", "location");
result.put("title", location.getVillage() != null ? location.getVillage() : location.getSector());
result.put("code", location.getCode());
result.put("locationType", "Village");
result.put("parent", location.getCell());
return result;
}

private Map<String, Object> mapWarehouseToResult(StorageWarehouse warehouse) {
Map<String, Object> result = new HashMap<>();
result.put("id", warehouse.getId());
result.put("type", "warehouse");
result.put("title", warehouse.getWarehouseName());
result.put("code", warehouse.getWarehouseCode());
result.put("warehouseType", warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().toString() : "");
result.put("location", warehouse.getLocation() != null && warehouse.getLocation().getVillage() != null ? warehouse.getLocation().getVillage() : "");
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
result.put("status", inventory.getStatus() != null ? inventory.getStatus().toString() : "");
return result;
}

private Map<String, Object> mapTransactionToResult(Transaction transaction) {
Map<String, Object> result = new HashMap<>();
result.put("id", transaction.getId());
result.put("type", "transaction");
result.put("title", transaction.getInventory() != null && transaction.getInventory().getCropType() != null ? transaction.getInventory().getCropType().getCropName() : "Transaction");
result.put("code", transaction.getTransactionCode());
result.put("status", transaction.getPaymentStatus() != null ? transaction.getPaymentStatus().toString() : "");
result.put("deliveryStatus", transaction.getDeliveryStatus() != null ? transaction.getDeliveryStatus().toString() : "");
result.put("totalAmount", transaction.getTotalAmount());
result.put("transactionDate", transaction.getTransactionDate());
return result;
}

private Map<String, Object> mapEnquiryToResult(Enquiry enquiry) {
Map<String, Object> result = new HashMap<>();
result.put("id", enquiry.getId());
result.put("type", "enquiry");
result.put("title", enquiry.getInventory() != null && enquiry.getInventory().getCropType() != null ? enquiry.getInventory().getCropType().getCropName() : "Enquiry");
result.put("code", enquiry.getEnquiryCode());
result.put("status", enquiry.getStatus() != null ? enquiry.getStatus().toString() : "");
result.put("proposedQuantityKg", enquiry.getProposedQuantityKg());
result.put("proposedPricePerKg", enquiry.getProposedPricePerKg());
result.put("message", enquiry.getMessage());
return result;
}

private Map<String, Object> mapMessageToResult(Message message) {
Map<String, Object> result = new HashMap<>();
result.put("id", message.getId());
result.put("type", "message");
result.put("title", message.getSubject());
result.put("code", message.getMessageCode());
result.put("content", message.getContent());
result.put("sender", message.getSender() != null ? message.getSender().getFirstName() + " " + message.getSender().getLastName() : "");
result.put("receiver", message.getReceiver() != null ? message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName() : "");
result.put("isRead", message.getIsRead());
return result;
}

private Map<String, Object> mapRatingToResult(Rating rating) {
Map<String, Object> result = new HashMap<>();
result.put("id", rating.getId());
result.put("type", "rating");
result.put("title", "Rating by " + (rating.getRater() != null ? rating.getRater().getFirstName() + " " + rating.getRater().getLastName() : ""));
result.put("ratingScore", rating.getRatingScore());
result.put("ratingType", rating.getRatingType() != null ? rating.getRatingType().toString() : "");
result.put("comment", rating.getComment());
result.put("ratedUser", rating.getRatedUser() != null ? rating.getRatedUser().getFirstName() + " " + rating.getRatedUser().getLastName() : "");
return result;
}

private Map<String, Object> mapNotificationToResult(Notification notification) {
Map<String, Object> result = new HashMap<>();
result.put("id", notification.getId());
result.put("type", "notification");
result.put("title", notification.getTitle());
result.put("message", notification.getMessage());
result.put("notificationType", notification.getType() != null ? notification.getType().toString() : "");
result.put("isRead", notification.getIsRead());
return result;
}

private Map<String, Object> mapInventoryRequestToResult(InventoryRequest request) {
Map<String, Object> result = new HashMap<>();
result.put("id", request.getId());
result.put("type", "inventory_request");
result.put("title", request.getInventory() != null && request.getInventory().getCropType() != null ? request.getInventory().getCropType().getCropName() : "Inventory Request");
result.put("code", request.getRequestCode());
result.put("status", request.getStatus() != null ? request.getStatus().toString() : "");
result.put("requestType", request.getRequestType() != null ? request.getRequestType().toString() : "");
result.put("farmerNotes", request.getFarmerNotes());
result.put("storekeeperResponse", request.getStorekeeperResponse());
return result;
}

private Map<String, Object> mapWarehouseAccessToResult(WarehouseAccess access) {
Map<String, Object> result = new HashMap<>();
result.put("id", access.getId());
result.put("type", "warehouse_access");
result.put("title", access.getWarehouse() != null ? access.getWarehouse().getWarehouseName() : "Warehouse Access");
result.put("status", access.getStatus() != null ? access.getStatus().toString() : "");
result.put("accessLevel", access.getAccessLevel() != null ? access.getAccessLevel().toString() : "");
result.put("user", access.getUser() != null ? access.getUser().getFirstName() + " " + access.getUser().getLastName() : "");
result.put("warehouse", access.getWarehouse() != null ? access.getWarehouse().getWarehouseName() : "");
return result;
}

private Map<String, Object> mapWithdrawalToResult(Withdrawal withdrawal) {
Map<String, Object> result = new HashMap<>();
result.put("id", withdrawal.getId());
result.put("type", "withdrawal");
result.put("title", "Withdrawal " + (withdrawal.getWithdrawalCode() != null ? withdrawal.getWithdrawalCode() : ""));
result.put("code", withdrawal.getWithdrawalCode());
result.put("status", withdrawal.getStatus() != null ? withdrawal.getStatus().toString() : "");
result.put("amount", withdrawal.getAmount());
result.put("accountNumber", withdrawal.getAccountNumber());
result.put("accountName", withdrawal.getAccountName());
return result;
}

private Map<String, Object> mapUserToResult(User user) {
Map<String, Object> result = new HashMap<>();
result.put("id", user.getId());
result.put("type", "user");
result.put("title", (user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""));
result.put("code", user.getUserCode());
result.put("email", user.getEmail());
result.put("userType", user.getUserType() != null ? user.getUserType().toString() : "");
result.put("phoneNumber", user.getPhoneNumber());
result.put("status", user.getStatus() != null ? user.getStatus().toString() : "");
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
