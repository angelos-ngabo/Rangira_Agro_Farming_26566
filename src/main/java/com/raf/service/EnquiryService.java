package com.raf.service;

import com.raf.dto.EnquiryRequest;
import com.raf.dto.EnquiryResponseRequest;
import com.raf.entity.Enquiry;
import com.raf.entity.Inventory;
import com.raf.entity.User;
import com.raf.enums.EnquiryStatus;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.EnquiryRepository;
import com.raf.repository.InventoryRepository;
import com.raf.repository.UserRepository;
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
public class EnquiryService {

private final EnquiryRepository enquiryRepository;
private final InventoryRepository inventoryRepository;
private final UserRepository userRepository;
private final MessageService messageService;
private final NotificationService notificationService;
private final PurchaseFlowService purchaseFlowService;

@PersistenceContext
private EntityManager entityManager;

public Enquiry createEnquiry(Long buyerId, EnquiryRequest request) {
log.info("Creating enquiry from buyer {} for inventory {}", buyerId, request.getInventoryId());

User buyer = userRepository.findById(buyerId)
.orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + buyerId));

Inventory inventory = inventoryRepository.findById(request.getInventoryId())
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + request.getInventoryId()));

User farmer = inventory.getFarmer();


if (request.getProposedQuantityKg().compareTo(inventory.getRemainingQuantityKg()) > 0) {
throw new IllegalArgumentException("Proposed quantity exceeds available inventory");
}


BigDecimal totalAmount = request.getProposedQuantityKg().multiply(request.getProposedPricePerKg());


String enquiryCode = "ENQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
while (enquiryRepository.existsByEnquiryCode(enquiryCode)) {
enquiryCode = "ENQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}

Enquiry enquiry = new Enquiry();
enquiry.setEnquiryCode(enquiryCode);
enquiry.setBuyer(buyer);
enquiry.setFarmer(farmer);
enquiry.setInventory(inventory);
enquiry.setProposedQuantityKg(request.getProposedQuantityKg());
enquiry.setProposedPricePerKg(request.getProposedPricePerKg());
enquiry.setProposedTotalAmount(totalAmount);
enquiry.setMessage(request.getMessage());
enquiry.setStatus(EnquiryStatus.PENDING);
enquiry.setEnquiryDate(LocalDateTime.now());

Enquiry savedEnquiry = enquiryRepository.save(enquiry);
entityManager.flush();


notificationService.createNotification(
farmer.getId(),
"New Enquiry Received",
String.format("You have received a new enquiry from %s %s for %s kg of %s at RWF %s per kg",
buyer.getFirstName(), buyer.getLastName(),
request.getProposedQuantityKg(),
inventory.getCropType().getCropName(),
request.getProposedPricePerKg()),
com.raf.enums.NotificationType.ENQUIRY_RECEIVED,
savedEnquiry.getId()
);

log.info("Enquiry created successfully: {}", enquiryCode);
return savedEnquiry;
}

public Enquiry respondToEnquiry(Long farmerId, EnquiryResponseRequest request) {
log.info("Farmer {} responding to enquiry {}", farmerId, request.getEnquiryId());

Enquiry enquiry = enquiryRepository.findById(request.getEnquiryId())
.orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with ID: " + request.getEnquiryId()));

if (!enquiry.getFarmer().getId().equals(farmerId)) {
throw new IllegalArgumentException("You are not authorized to respond to this enquiry");
}

if (enquiry.getStatus() != EnquiryStatus.PENDING) {
throw new IllegalArgumentException("Enquiry is no longer pending");
}

if (request.getAccept()) {
enquiry.setStatus(EnquiryStatus.ACCEPTED);
enquiry.setResponseMessage(request.getResponseMessage());
enquiry.setResponseDate(LocalDateTime.now());

Enquiry savedEnquiry = enquiryRepository.save(enquiry);
entityManager.flush();


com.raf.entity.Transaction transaction = purchaseFlowService.createTransactionFromEnquiry(savedEnquiry.getId());


entityManager.refresh(savedEnquiry);


notificationService.createNotification(
enquiry.getBuyer().getId(),
"Enquiry Accepted - Proceed to Payment",
String.format("Your enquiry for %s kg of %s has been accepted by %s %s. Total amount: RWF %s. Please proceed to payment.",
enquiry.getProposedQuantityKg(),
enquiry.getInventory().getCropType().getCropName(),
enquiry.getFarmer().getFirstName(),
enquiry.getFarmer().getLastName(),
enquiry.getProposedTotalAmount().toPlainString()),
com.raf.enums.NotificationType.ENQUIRY_ACCEPTED,
transaction.getId(),
"/payment/" + transaction.getId()
);

log.info("Enquiry accepted and transaction created: {} -> {}", enquiry.getEnquiryCode(), transaction.getTransactionCode());


Enquiry enquiryWithTransaction = enquiryRepository.findById(savedEnquiry.getId())
.orElse(savedEnquiry);
if (enquiryWithTransaction.getTransaction() != null) {
enquiryWithTransaction.getTransaction().getId();
}

return enquiryWithTransaction;
} else {
enquiry.setStatus(EnquiryStatus.REJECTED);
enquiry.setResponseMessage(request.getResponseMessage());
enquiry.setResponseDate(LocalDateTime.now());

Enquiry savedEnquiry = enquiryRepository.save(enquiry);
entityManager.flush();


notificationService.createNotification(
enquiry.getBuyer().getId(),
"Enquiry Rejected",
String.format("Your enquiry for %s has been rejected by the farmer.",
enquiry.getInventory().getCropType().getCropName()),
com.raf.enums.NotificationType.ENQUIRY_REJECTED,
null
);

log.info("Enquiry rejected: {}", enquiry.getEnquiryCode());
return savedEnquiry;
}
}

@Transactional(readOnly = true)
public List<Enquiry> getEnquiriesByBuyer(Long buyerId) {
List<Enquiry> enquiries = enquiryRepository.findByBuyerId(buyerId);

enquiries.forEach(e -> {
if (e.getTransaction() != null) {
e.getTransaction().getId();
}
});
return enquiries;
}

@Transactional(readOnly = true)
public List<Enquiry> getEnquiriesByFarmer(Long farmerId) {
List<Enquiry> enquiries = enquiryRepository.findByFarmerId(farmerId);

enquiries.forEach(e -> {
if (e.getTransaction() != null) {
e.getTransaction().getId();
}
});
return enquiries;
}

@Transactional(readOnly = true)
public Enquiry getEnquiryById(Long id) {
return enquiryRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with ID: " + id));
}
}

