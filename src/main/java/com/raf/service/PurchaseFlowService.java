package com.raf.service;

import com.raf.dto.PaymentRequest;
import com.raf.entity.*;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.EnquiryStatus;
import com.raf.enums.PaymentMethod;
import com.raf.enums.PaymentStatus;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PurchaseFlowService {

private final EnquiryRepository enquiryRepository;
private final TransactionRepository transactionRepository;
private final PaymentRepository paymentRepository;
private final InventoryRepository inventoryRepository;
private final UserRepository userRepository;
private final WalletRepository walletRepository;
private final NotificationRepository notificationRepository;
private final EmailService emailService;
private final InvoiceService invoiceService;
private final NotificationService notificationService;
private final com.raf.repository.WarehouseAccessRepository warehouseAccessRepository;

@PersistenceContext
private EntityManager entityManager;


public Transaction createTransactionFromEnquiry(Long enquiryId) {
log.info("Creating transaction from enquiry {}", enquiryId);

Enquiry enquiry = enquiryRepository.findById(enquiryId)
.orElseThrow(() -> new ResourceNotFoundException("Enquiry not found"));

if (enquiry.getStatus() != EnquiryStatus.ACCEPTED) {
throw new IllegalArgumentException("Enquiry must be accepted before creating transaction");
}

if (enquiry.getTransaction() != null) {
throw new IllegalArgumentException("Transaction already exists for this enquiry");
}


String transactionCode = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
while (transactionRepository.existsByTransactionCode(transactionCode)) {
transactionCode = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}


BigDecimal totalAmount = enquiry.getProposedTotalAmount();
BigDecimal serviceFee = totalAmount.multiply(new BigDecimal("0.05"));
BigDecimal netAmount = totalAmount.subtract(serviceFee);


BigDecimal storageFee = BigDecimal.ZERO;
BigDecimal transactionFee = BigDecimal.ZERO;
BigDecimal commission = serviceFee;

Transaction transaction = new Transaction();
transaction.setTransactionCode(transactionCode);
transaction.setInventory(enquiry.getInventory());
transaction.setBuyer(enquiry.getBuyer());
transaction.setSeller(enquiry.getFarmer());
transaction.setQuantityKg(enquiry.getProposedQuantityKg());
transaction.setUnitPrice(enquiry.getProposedPricePerKg());
transaction.setTotalAmount(totalAmount);
transaction.setStorageFee(storageFee);
transaction.setTransactionFee(transactionFee);
transaction.setCommission(commission);
transaction.setNetAmount(netAmount);
transaction.setPaymentStatus(PaymentStatus.PENDING);
transaction.setDeliveryStatus(DeliveryStatus.PENDING);
transaction.setTransactionDate(LocalDateTime.now());
transaction.setEnquiry(enquiry);

Transaction savedTransaction = transactionRepository.save(transaction);
enquiry.setTransaction(savedTransaction);
Enquiry savedEnquiry = enquiryRepository.save(enquiry);
entityManager.flush();

log.info("Transaction created: {} for enquiry: {}", transactionCode, enquiry.getEnquiryCode());
return savedTransaction;
}


public Payment processPayment(Long buyerId, PaymentRequest request) {
log.info("Processing payment for transaction {} by buyer {}", request.getTransactionId(), buyerId);

Transaction transaction = transactionRepository.findById(request.getTransactionId())
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

if (!transaction.getBuyer().getId().equals(buyerId)) {
throw new IllegalArgumentException("Unauthorized to pay for this transaction");
}

if (transaction.getPaymentStatus() == PaymentStatus.PAID) {
throw new IllegalArgumentException("Transaction already paid");
}


List<Payment> existingPayments = paymentRepository.findByTransactionId(transaction.getId());
if (!existingPayments.isEmpty()) {
Payment existingPayment = existingPayments.get(0);
log.warn("Payment already exists for transaction {}. Existing payment: {}",
transaction.getId(), existingPayment.getPaymentCode());

if (transaction.getPaymentStatus() != PaymentStatus.PAID) {
transaction.setPaymentStatus(PaymentStatus.PAID);
transaction.setPaymentDate(LocalDateTime.now());
transactionRepository.save(transaction);
entityManager.flush();
}

log.info("Returning existing payment {} for transaction {}", existingPayment.getPaymentCode(), transaction.getId());
return existingPayment;
}


if (request.getAmount().compareTo(transaction.getTotalAmount()) != 0) {
throw new IllegalArgumentException("Payment amount must match transaction total amount");
}


if (request.getPaymentMethod() == PaymentMethod.MOBILE_MONEY &&
(request.getPaymentReference() == null || request.getPaymentReference().trim().isEmpty())) {
throw new IllegalArgumentException("Mobile Money transaction reference is required");
}


String paymentCode = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
while (paymentRepository.existsByPaymentCode(paymentCode)) {
paymentCode = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}


Payment payment = new Payment();
payment.setPaymentCode(paymentCode);
payment.setTransaction(transaction);
payment.setPayer(transaction.getBuyer());
payment.setAmount(request.getAmount());
payment.setPaymentMethod(request.getPaymentMethod());
payment.setPaymentReference(request.getPaymentReference());
payment.setStatus(PaymentStatus.PAID);
payment.setPaymentDate(LocalDateTime.now());
payment.setProcessedDate(LocalDateTime.now());
payment.setNotes(request.getNotes());


Payment savedPayment = paymentRepository.save(payment);

transaction.setPaymentStatus(PaymentStatus.PAID);
transaction.setPaymentDate(LocalDateTime.now());
transactionRepository.save(transaction);

Inventory inventory = transaction.getInventory();
BigDecimal remaining = inventory.getRemainingQuantityKg().subtract(transaction.getQuantityKg());
inventory.setRemainingQuantityKg(remaining);
if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
inventory.setStatus(com.raf.enums.InventoryStatus.SOLD);
} else {
inventory.setStatus(com.raf.enums.InventoryStatus.PARTIALLY_SOLD);
}
inventoryRepository.save(inventory);

Wallet farmerWallet = walletRepository.findByUserId(transaction.getSeller().getId())
.orElseGet(() -> {
Wallet newWallet = new Wallet();
newWallet.setUser(transaction.getSeller());
newWallet.setBalance(BigDecimal.ZERO);
newWallet.setTotalEarned(BigDecimal.ZERO);
newWallet.setTotalWithdrawn(BigDecimal.ZERO);
return walletRepository.save(newWallet);
});

BigDecimal netEarnings = transaction.getNetAmount();
farmerWallet.setBalance(farmerWallet.getBalance().add(netEarnings));
farmerWallet.setTotalEarned(farmerWallet.getTotalEarned().add(netEarnings));
walletRepository.save(farmerWallet);

entityManager.flush();

Transaction finalTransaction = transaction;
Inventory finalInventory = inventory;
StorageWarehouse warehouse = inventory.getWarehouse();

java.util.List<com.raf.entity.WarehouseAccess> storekeeperAccesses = 
warehouseAccessRepository.findByWarehouseIdAndAccessLevelAndIsActive(
warehouse.getId(), 
com.raf.enums.AccessLevel.MANAGER, 
true);

final User finalAssignedStorekeeper = !storekeeperAccesses.isEmpty() 
? storekeeperAccesses.get(0).getUser() 
: null;

notificationService.createNotification(
finalTransaction.getSeller().getId(),
"Payment Received - Earnings Credited",
String.format("Your sale of %s kg of %s has been paid. Gross: RWF %s, Service Fee (5%%): RWF %s, Net Earnings: RWF %s. Amount credited to your wallet.",
finalTransaction.getQuantityKg(),
finalInventory.getCropType().getCropName(),
finalTransaction.getTotalAmount().toPlainString(),
finalTransaction.getCommission().toPlainString(),
netEarnings.toPlainString()),
com.raf.enums.NotificationType.PAYMENT_RECEIVED,
finalTransaction.getId()
);

notificationService.createNotification(
finalTransaction.getBuyer().getId(),
"Payment Successful",
String.format("Your payment of RWF %s for %s kg of %s was successful. Please await delivery.",
finalTransaction.getTotalAmount().toPlainString(),
finalTransaction.getQuantityKg(),
finalInventory.getCropType().getCropName()),
com.raf.enums.NotificationType.PAYMENT_RECEIVED,
finalTransaction.getId(),
"/receipts"
);

if (finalAssignedStorekeeper != null) {
notificationService.createNotification(
finalAssignedStorekeeper.getId(),
"Shipment Request - Action Required",
String.format("A new order requires shipment: %s kg of %s to %s %s. Transaction: %s. Please process delivery.",
finalTransaction.getQuantityKg(),
finalInventory.getCropType().getCropName(),
finalTransaction.getBuyer().getFirstName(),
finalTransaction.getBuyer().getLastName(),
finalTransaction.getTransactionCode()),
com.raf.enums.NotificationType.SHIPMENT_REQUEST,
finalTransaction.getId()
);
}

log.info("Payment processed successfully: {}", paymentCode);

new java.lang.Thread(() -> {
try {
invoiceService.sendInvoiceToBuyer(finalTransaction);
emailService.sendPurchaseConfirmationToBuyer(finalTransaction);
log.info("Payment confirmation email sent to buyer: {}", finalTransaction.getBuyer().getEmail());
} catch (Exception e) {
log.warn("Failed to send buyer email: {}", e.getMessage());
}
}).start();

new java.lang.Thread(() -> {
try {
emailService.sendPurchaseConfirmationToFarmer(finalTransaction);
} catch (Exception e) {
log.warn("Failed to send farmer confirmation email: {}", e.getMessage());
}
}).start();

if (finalAssignedStorekeeper != null) {
new java.lang.Thread(() -> {
try {
emailService.sendDeliveryReminderToStorekeeper(finalTransaction, finalAssignedStorekeeper, warehouse);
log.info("Delivery reminder email sent to storekeeper: {}", finalAssignedStorekeeper.getEmail());
} catch (Exception e) {
log.warn("Failed to send storekeeper reminder email: {}", e.getMessage());
}
}).start();
} else {
log.warn("No storekeeper found for warehouse: {}", warehouse.getWarehouseName());
}

return savedPayment;
}
}

