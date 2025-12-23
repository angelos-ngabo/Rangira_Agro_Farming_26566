package com.raf.service;

import com.raf.dto.TransactionRequest;
import com.raf.entity.Inventory;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.PaymentStatus;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.InventoryRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import com.raf.entity.WarehouseAccess;
import com.raf.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
@PersistenceContext
private EntityManager entityManager;

private final TransactionRepository transactionRepository;
private final UserRepository userRepository;
private final InventoryRepository inventoryRepository;
private final NotificationService notificationService;
private final EmailService emailService;
private final ClearanceDocumentService clearanceDocumentService;
private final WarehouseAccessRepository warehouseAccessRepository;

public Transaction createTransactionFromRequest(TransactionRequest request) {
log.info("Creating transaction from request: {}", request.getTransactionCode());

if (transactionRepository.existsByTransactionCode(request.getTransactionCode())) {
throw new DuplicateResourceException("Transaction with code " + request.getTransactionCode() + " already exists");
}

Inventory inventory = inventoryRepository.findById(request.getInventoryId())
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + request.getInventoryId()));

User buyer = userRepository.findById(request.getBuyerId())
.orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + request.getBuyerId()));

User seller = userRepository.findById(request.getSellerId())
.orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + request.getSellerId()));

Transaction transaction = new Transaction();
transaction.setTransactionCode(request.getTransactionCode());
transaction.setInventory(inventory);
transaction.setBuyer(buyer);
transaction.setSeller(seller);
transaction.setQuantityKg(request.getQuantityKg());
transaction.setUnitPrice(request.getUnitPrice());
transaction.setTotalAmount(request.getTotalAmount());
transaction.setStorageFee(request.getStorageFee() != null ? request.getStorageFee() : BigDecimal.ZERO);
transaction.setTransactionFee(request.getTransactionFee() != null ? request.getTransactionFee() : BigDecimal.ZERO);
transaction.setNetAmount(request.getNetAmount());
transaction.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PENDING);
transaction.setDeliveryStatus(request.getDeliveryStatus() != null ? request.getDeliveryStatus() : DeliveryStatus.PENDING);
transaction.setTransactionDate(request.getTransactionDate());
transaction.setPaymentDate(request.getPaymentDate());
transaction.setNotes(request.getNotes());

return transactionRepository.save(transaction);
}


private List<Long> getAssignedWarehouseIds(Long storekeeperId) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true);
return warehouseAccesses.stream()
.filter(access -> access.getStatus() == com.raf.enums.WarehouseAccessStatus.APPROVED ||
access.getStatus() == com.raf.enums.WarehouseAccessStatus.ACTIVE)
.map(access -> access.getWarehouse().getId())
.distinct()
.toList();
}


private List<Transaction> filterTransactionsByWarehouse(List<Transaction> transactions, Long storekeeperId) {
if (storekeeperId == null) {
log.debug("No storekeeperId provided, returning all transactions without filtering");
return transactions;
}

User user = userRepository.findById(storekeeperId).orElse(null);
if (user == null) {
log.warn("User not found for ID: {}, returning all transactions without filtering", storekeeperId);
return transactions;
}

if (user.getUserType() != UserType.STOREKEEPER) {
log.debug("User {} is not a storekeeper (type: {}), returning all transactions without filtering",
storekeeperId, user.getUserType());
return transactions;
}

List<Long> assignedWarehouseIds = getAssignedWarehouseIds(storekeeperId);
if (assignedWarehouseIds.isEmpty()) {
log.info("Storekeeper {} has no assigned warehouses, returning empty transaction list", storekeeperId);
return List.of();
}

log.info("Filtering {} transactions for storekeeper {} by {} assigned warehouses: {}",
transactions.size(), storekeeperId, assignedWarehouseIds.size(), assignedWarehouseIds);

List<Transaction> filtered = transactions.stream()
.filter(t -> {
if (t.getInventory() == null) {
log.debug("Transaction {} has no inventory, filtering out", t.getId());
return false;
}
if (t.getInventory().getWarehouse() == null) {
log.debug("Transaction {} inventory has no warehouse, filtering out", t.getId());
return false;
}
Long warehouseId = t.getInventory().getWarehouse().getId();
boolean matches = assignedWarehouseIds.contains(warehouseId);
if (!matches) {
log.debug("Transaction {} warehouse {} not in storekeeper's assigned warehouses, filtering out",
t.getId(), warehouseId);
}
return matches;
})
.toList();

log.info("Filtered {} transactions down to {} for storekeeper {}",
transactions.size(), filtered.size(), storekeeperId);
return filtered;
}

@Transactional(readOnly = true)
public List<Transaction> getAllTransactions() {
return getAllTransactions(null);
}

@Transactional(readOnly = true)
public List<Transaction> getAllTransactions(Long userId) {
log.info("📊 Fetching all transactions from database{}...", userId != null ? " for user " + userId : "");
List<Transaction> transactions = transactionRepository.findAll();


if (userId != null) {
transactions = filterTransactionsByWarehouse(transactions, userId);
}

log.info("✅ Successfully fetched {} transactions from database", transactions.size());
if (transactions.isEmpty()) {
log.warn("⚠️  WARNING: No transactions found in database. Database may be empty or transactions table is not populated.");
} else {
log.info("📋 Transactions found: {}", transactions.stream()
.map(t -> String.format("%s (Status: %s, Amount: %s)", t.getTransactionCode(), t.getPaymentStatus(), t.getTotalAmount()))
.limit(5)
.collect(java.util.stream.Collectors.joining(", ")) + (transactions.size() > 5 ? "..." : ""));
}

transactions.forEach(t -> {
if (t.getBuyer() != null) {
t.getBuyer().getId();
t.getBuyer().getFirstName();
t.getBuyer().getLastName();
}
if (t.getSeller() != null) {
t.getSeller().getId();
t.getSeller().getFirstName();
t.getSeller().getLastName();
}
if (t.getInventory() != null) {
t.getInventory().getId();
if (t.getInventory().getCropType() != null) {
t.getInventory().getCropType().getId();
t.getInventory().getCropType().getCropName();
}
}
});
return transactions;
}

@Transactional(readOnly = true)
public Page<Transaction> getTransactionsPaginated(Pageable pageable) {
return getTransactionsPaginated(pageable, null);
}

@Transactional(readOnly = true)
public Page<Transaction> getTransactionsPaginated(Pageable pageable, Long userId) {
log.info("📊 Fetching paginated transactions from database (page: {}, size: {}){}...",
pageable.getPageNumber(), pageable.getPageSize(), userId != null ? " for user " + userId : "");
Page<Transaction> transactions = transactionRepository.findAll(pageable);


if (userId != null) {
List<Transaction> filtered = filterTransactionsByWarehouse(transactions.getContent(), userId);

transactions = new org.springframework.data.domain.PageImpl<>(
filtered,
pageable,
filtered.size()
);
}

log.info("✅ Successfully fetched {} transactions from database (page {} of {}, total: {})",
transactions.getNumberOfElements(),
transactions.getNumber() + 1,
transactions.getTotalPages(),
transactions.getTotalElements());
if (transactions.isEmpty()) {
log.warn("⚠️  WARNING: No transactions found in database for page {}. Database may be empty or transactions table is not populated.", pageable.getPageNumber());
}

transactions.getContent().forEach(t -> {
if (t.getBuyer() != null) {
t.getBuyer().getId();
t.getBuyer().getFirstName();
t.getBuyer().getLastName();
}
if (t.getSeller() != null) {
t.getSeller().getId();
t.getSeller().getFirstName();
t.getSeller().getLastName();
}
if (t.getInventory() != null) {
t.getInventory().getId();
if (t.getInventory().getCropType() != null) {
t.getInventory().getCropType().getId();
t.getInventory().getCropType().getCropName();
}
}
});
return transactions;
}

@Transactional(readOnly = true)
public Transaction getTransactionById(Long id) {

Transaction transaction = transactionRepository.findByIdWithInventoryAndRelations(id)
.orElseGet(() -> {

return transactionRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
});


if (transaction.getInventory() != null) {
transaction.getInventory().getId();
if (transaction.getInventory().getCropType() != null) {
transaction.getInventory().getCropType().getId();
transaction.getInventory().getCropType().getCropName();
}
}
if (transaction.getBuyer() != null) {
transaction.getBuyer().getId();
transaction.getBuyer().getFirstName();
transaction.getBuyer().getLastName();
}
if (transaction.getSeller() != null) {
transaction.getSeller().getId();
transaction.getSeller().getFirstName();
transaction.getSeller().getLastName();
}

return transaction;
}

@Transactional(readOnly = true)
public Transaction getTransactionByCode(String transactionCode) {
return transactionRepository.findByTransactionCode(transactionCode)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with code: " + transactionCode));
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByBuyer(Long buyerId) {
List<Transaction> transactions = transactionRepository.findByBuyerId(buyerId);

transactions.forEach(t -> {
if (t.getBuyer() != null) {
t.getBuyer().getId();
t.getBuyer().getFirstName();
t.getBuyer().getLastName();
}
if (t.getSeller() != null) {
t.getSeller().getId();
t.getSeller().getFirstName();
t.getSeller().getLastName();
}
if (t.getInventory() != null) {
t.getInventory().getId();
if (t.getInventory().getCropType() != null) {
t.getInventory().getCropType().getId();
t.getInventory().getCropType().getCropName();
}
}
});
return transactions;
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsBySeller(Long sellerId) {
log.info("📊 Fetching transactions for seller ID: {}...", sellerId);
List<Transaction> transactions = transactionRepository.findBySellerId(sellerId);


transactions = filterTransactionsByWarehouse(transactions, sellerId);

log.info("✅ Successfully fetched {} transactions for seller ID: {}", transactions.size(), sellerId);
if (transactions.isEmpty()) {
log.info("ℹ️  No transactions found for seller ID: {}", sellerId);
}

transactions.forEach(t -> {
if (t.getBuyer() != null) {
t.getBuyer().getId();
t.getBuyer().getFirstName();
t.getBuyer().getLastName();
}
if (t.getSeller() != null) {
t.getSeller().getId();
t.getSeller().getFirstName();
t.getSeller().getLastName();
}
if (t.getInventory() != null) {
t.getInventory().getId();
if (t.getInventory().getCropType() != null) {
t.getInventory().getCropType().getId();
t.getInventory().getCropType().getCropName();
}
}
});
return transactions;
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByInventory(Long inventoryId) {
return getTransactionsByInventoryForUser(inventoryId, null);
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByInventoryForUser(Long inventoryId, Long userId) {
List<Transaction> transactions = transactionRepository.findByInventoryId(inventoryId);


if (userId != null) {
transactions = filterTransactionsByWarehouse(transactions, userId);
}

return transactions;
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus) {
return getTransactionsByPaymentStatusForUser(paymentStatus, null);
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByPaymentStatusForUser(PaymentStatus paymentStatus, Long userId) {
List<Transaction> transactions = transactionRepository.findByPaymentStatus(paymentStatus);


if (userId != null) {
transactions = filterTransactionsByWarehouse(transactions, userId);
}

return transactions;
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus, Sort sort) {
return getTransactionsByPaymentStatusForUser(paymentStatus, sort, null);
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByPaymentStatusForUser(PaymentStatus paymentStatus, Sort sort, Long userId) {
List<Transaction> transactions = transactionRepository.findByPaymentStatus(paymentStatus, sort);


if (userId != null) {
transactions = filterTransactionsByWarehouse(transactions, userId);
}

return transactions;
}

@Transactional(readOnly = true)
public Page<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable) {
return getTransactionsByPaymentStatusForUser(paymentStatus, pageable, null);
}

@Transactional(readOnly = true)
public Page<Transaction> getTransactionsByPaymentStatusForUser(PaymentStatus paymentStatus, Pageable pageable, Long userId) {
Page<Transaction> transactions = transactionRepository.findByPaymentStatus(paymentStatus, pageable);


if (userId != null) {
List<Transaction> filtered = filterTransactionsByWarehouse(transactions.getContent(), userId);
transactions = new org.springframework.data.domain.PageImpl<>(
filtered,
pageable,
filtered.size()
);
}

return transactions;
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByDeliveryStatus(DeliveryStatus deliveryStatus) {
return getTransactionsByDeliveryStatusForUser(deliveryStatus, null);
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByDeliveryStatusForUser(DeliveryStatus deliveryStatus, Long userId) {
List<Transaction> transactions = transactionRepository.findByDeliveryStatus(deliveryStatus);


if (userId != null) {
transactions = filterTransactionsByWarehouse(transactions, userId);
}

return transactions;
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByDeliveryStatus(DeliveryStatus deliveryStatus, Sort sort) {
return getTransactionsByDeliveryStatusForUser(deliveryStatus, sort, null);
}

@Transactional(readOnly = true)
public List<Transaction> getTransactionsByDeliveryStatusForUser(DeliveryStatus deliveryStatus, Sort sort, Long userId) {
List<Transaction> transactions = transactionRepository.findByDeliveryStatus(deliveryStatus, sort);


if (userId != null) {
transactions = filterTransactionsByWarehouse(transactions, userId);
}

return transactions;
}

@Transactional(readOnly = true)
public Page<Transaction> getTransactionsByBuyer(Long buyerId, Pageable pageable) {
return transactionRepository.findByBuyerId(buyerId, pageable);
}

@Transactional(readOnly = true)
public Page<Transaction> getTransactionsBySeller(Long sellerId, Pageable pageable) {
return transactionRepository.findBySellerId(sellerId, pageable);
}

public Transaction updateTransaction(Long id, Transaction transactionDetails) {
Transaction transaction = transactionRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

transaction.setQuantityKg(transactionDetails.getQuantityKg());
transaction.setUnitPrice(transactionDetails.getUnitPrice());
transaction.setTotalAmount(transactionDetails.getTotalAmount());
transaction.setPaymentStatus(transactionDetails.getPaymentStatus());
transaction.setDeliveryStatus(transactionDetails.getDeliveryStatus());
transaction.setNotes(transactionDetails.getNotes());

log.info("Updating transaction ID: {}", id);
return transactionRepository.save(transaction);
}

public Transaction updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
Transaction transaction = transactionRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
transaction.setPaymentStatus(paymentStatus);

if (paymentStatus == PaymentStatus.PAID) {
transaction.setPaymentDate(LocalDateTime.now());
}

log.info("Updating transaction ID {} payment status to: {}", id, paymentStatus);
return transactionRepository.save(transaction);
}

public Transaction updateDeliveryStatus(Long id, DeliveryStatus deliveryStatus) {
return updateDeliveryStatus(id, deliveryStatus, null);
}

public Transaction updateDeliveryStatus(Long id, DeliveryStatus deliveryStatus, Long userId) {
Transaction transaction = transactionRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));


if (userId != null) {
User user = userRepository.findById(userId).orElse(null);
if (user != null && user.getUserType() == UserType.STOREKEEPER) {
if (transaction.getInventory() == null || transaction.getInventory().getWarehouse() == null) {
throw new IllegalArgumentException("Transaction inventory or warehouse not found");
}
Long warehouseId = transaction.getInventory().getWarehouse().getId();
boolean hasAccess = warehouseAccessRepository.existsByUserIdAndWarehouseIdAndIsActive(userId, warehouseId, true);
if (!hasAccess) {
log.warn("Storekeeper {} attempted to update delivery status for transaction {} in warehouse {} without access",
userId, id, warehouseId);
throw new IllegalArgumentException("Unauthorized: You can only update delivery status for transactions in your assigned warehouses");
}

warehouseAccessRepository.findByUserIdAndWarehouseId(userId, warehouseId)
.ifPresent(access -> {
if (access.getStatus() != com.raf.enums.WarehouseAccessStatus.APPROVED &&
access.getStatus() != com.raf.enums.WarehouseAccessStatus.ACTIVE) {
throw new IllegalArgumentException("Unauthorized: Your warehouse access is not active");
}
});
}
}

DeliveryStatus oldStatus = transaction.getDeliveryStatus();


if (oldStatus == com.raf.enums.DeliveryStatus.DELIVERED && deliveryStatus != com.raf.enums.DeliveryStatus.DELIVERED) {
throw new IllegalArgumentException("Cannot change status from DELIVERED to " + deliveryStatus);
}

if (oldStatus == com.raf.enums.DeliveryStatus.CANCELLED && deliveryStatus != com.raf.enums.DeliveryStatus.CANCELLED) {
throw new IllegalArgumentException("Cannot change status from CANCELLED to " + deliveryStatus);
}


if (oldStatus == deliveryStatus) {
log.info("Transaction ID {} already has delivery status: {}", id, deliveryStatus);
return transaction;
}

transaction.setDeliveryStatus(deliveryStatus);
Transaction saved = transactionRepository.save(transaction);
entityManager.flush();
entityManager.refresh(saved);

log.info("Updated transaction ID {} delivery status from {} to: {}", id, oldStatus, deliveryStatus);


if (deliveryStatus == com.raf.enums.DeliveryStatus.DELIVERED && oldStatus != com.raf.enums.DeliveryStatus.DELIVERED) {
try {

Transaction freshTransaction = transactionRepository.findByIdWithInventoryAndRelations(id)
.orElse(saved);


notificationService.createNotification(
freshTransaction.getBuyer().getId(),
"Order Delivered",
String.format("Your order for %s kg of %s has been delivered successfully. Transaction: %s. Please rate your purchase!",
freshTransaction.getQuantityKg(),
freshTransaction.getInventory().getCropType().getCropName(),
freshTransaction.getTransactionCode()),
com.raf.enums.NotificationType.ORDER_DELIVERED,
freshTransaction.getId(),
"/dashboard"
);


notificationService.createNotification(
freshTransaction.getSeller().getId(),
"Order Delivered",
String.format("Your order of %s kg of %s has been delivered to %s %s. Transaction: %s",
freshTransaction.getQuantityKg(),
freshTransaction.getInventory().getCropType().getCropName(),
freshTransaction.getBuyer().getFirstName(),
freshTransaction.getBuyer().getLastName(),
freshTransaction.getTransactionCode()),
com.raf.enums.NotificationType.ORDER_DELIVERED,
freshTransaction.getId(),
"/transactions"
);


if (freshTransaction.getInventory() != null && freshTransaction.getInventory().getWarehouse() != null) {
StorageWarehouse warehouse = freshTransaction.getInventory().getWarehouse();
List<User> storekeepers = userRepository.findByUserType(com.raf.enums.UserType.STOREKEEPER);
for (User storekeeper : storekeepers) {
boolean hasAccess = storekeeper.getWarehouseAccesses().stream()
.anyMatch(wa -> wa.getWarehouse().getId().equals(warehouse.getId()) && wa.getIsActive());
if (hasAccess) {
notificationService.createNotification(
storekeeper.getId(),
"Order Delivered",
String.format("Buyer %s %s has confirmed delivery of %s kg of %s. Transaction: %s",
freshTransaction.getBuyer().getFirstName(),
freshTransaction.getBuyer().getLastName(),
freshTransaction.getQuantityKg(),
freshTransaction.getInventory().getCropType().getCropName(),
freshTransaction.getTransactionCode()),
com.raf.enums.NotificationType.ORDER_DELIVERED,
freshTransaction.getId(),
"/storekeeper/shipments"
);
break;
}
}
}

log.info("✅ Delivery notifications sent for transaction {}", id);
} catch (Exception e) {
log.warn("Failed to send delivery notification: {}", e.getMessage(), e);

}
}


if (deliveryStatus == com.raf.enums.DeliveryStatus.SHIPPED && oldStatus != com.raf.enums.DeliveryStatus.SHIPPED) {
try {

Transaction freshTransaction = transactionRepository.findByIdWithInventoryAndRelations(id)
.orElse(saved);

notificationService.createNotification(
freshTransaction.getBuyer().getId(),
"Order Shipped",
String.format("Your order for %s kg of %s has been shipped and is on the way. Transaction: %s",
freshTransaction.getQuantityKg(),
freshTransaction.getInventory().getCropType().getCropName(),
freshTransaction.getTransactionCode()),
com.raf.enums.NotificationType.ORDER_SHIPPED,
freshTransaction.getId(),
"/dashboard"
);


notificationService.createNotification(
freshTransaction.getSeller().getId(),
"Order Shipped",
String.format("Your order of %s kg of %s has been shipped to %s %s. Transaction: %s",
freshTransaction.getQuantityKg(),
freshTransaction.getInventory().getCropType().getCropName(),
freshTransaction.getBuyer().getFirstName(),
freshTransaction.getBuyer().getLastName(),
freshTransaction.getTransactionCode()),
com.raf.enums.NotificationType.ORDER_SHIPPED,
freshTransaction.getId(),
"/transactions"
);


try {
byte[] clearancePdf = clearanceDocumentService.generateClearancePdf(freshTransaction);
emailService.sendShipmentNotificationToBuyer(freshTransaction, clearancePdf);
log.info("✅ Shipment notification with PDF clearance sent to buyer for transaction {}", id);
} catch (Exception e) {
log.error("Failed to generate or send clearance PDF for transaction {}: {}", id, e.getMessage(), e);

try {
emailService.sendShipmentNotificationToBuyer(freshTransaction, null);
} catch (Exception emailEx) {
log.error("Failed to send shipment notification email: {}", emailEx.getMessage());
}
}
} catch (Exception e) {
log.warn("Failed to send shipment notification: {}", e.getMessage(), e);

}
}

return saved;
}

public void deleteTransaction(Long id) {
Transaction transaction = transactionRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
log.info("Deleting transaction ID: {}", id);
transactionRepository.delete(transaction);
}

@Transactional(readOnly = true)
public boolean transactionCodeExists(String transactionCode) {
return transactionRepository.existsByTransactionCode(transactionCode);
}

@Transactional(readOnly = true)
public long getTotalTransactions() {
return transactionRepository.count();
}

@Transactional(readOnly = true)
public long countTransactionsByBuyer(Long buyerId) {
return getTransactionsByBuyer(buyerId).size();
}

@Transactional(readOnly = true)
public long countTransactionsBySeller(Long sellerId) {
return getTransactionsBySeller(sellerId).size();
}

@Transactional(readOnly = true)
public BigDecimal getTotalSystemCommission() {
BigDecimal total = transactionRepository.getTotalSystemCommission();
return total != null ? total : BigDecimal.ZERO;
}
}

