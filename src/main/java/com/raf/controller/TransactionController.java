package com.raf.controller;

import com.raf.entity.Transaction;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.PaymentStatus;
import com.raf.service.InvoiceService;
import com.raf.service.TransactionService;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction", description = "Transaction management APIs (Read-only for Admin)")
public class TransactionController {
private final TransactionService transactionService;
private final InvoiceService invoiceService;
private final JwtUtil jwtUtil;
private final UserService userService;







@GetMapping
@Operation(summary = "Get all transactions with optional pagination")
public ResponseEntity<?> getAllTransactions(
@RequestParam(required = false) Integer page,
@RequestParam(required = false) Integer size,
jakarta.servlet.http.HttpServletRequest request) {

Long userId = null;
try {
userId = getCurrentUserId(request);
} catch (Exception e) {

log.debug("No authenticated user for transaction filtering: {}", e.getMessage());
}


if (page != null && size != null) {
log.info("🌐 API Request: GET /api/transactions - Fetching transactions (page: {}, size: {}){}",
page, size, userId != null ? " for user " + userId : "");
PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
Page<Transaction> transactions = transactionService.getTransactionsPaginated(pageRequest, userId);
log.info("✅ API Response: Returning {} transactions to client (page {} of {}, total: {})",
transactions.getNumberOfElements(),
transactions.getNumber() + 1,
transactions.getTotalPages(),
transactions.getTotalElements());
return ResponseEntity.ok(transactions);
}


log.info("🌐 API Request: GET /api/transactions - Fetching all transactions{}", userId != null ? " for user " + userId : "");
List<Transaction> transactions = transactionService.getAllTransactions(userId);
log.info("✅ API Response: Returning {} transactions to client", transactions.size());
return ResponseEntity.ok(transactions);
}

@GetMapping("/paginated")
@Operation(summary = "Get transactions with pagination")
public ResponseEntity<Page<Transaction>> getTransactionsPaginated(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size,
jakarta.servlet.http.HttpServletRequest request) {

Long userId = null;
try {
userId = getCurrentUserId(request);
} catch (Exception e) {

log.debug("No authenticated user for transaction filtering: {}", e.getMessage());
}

PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
Page<Transaction> transactions = transactionService.getTransactionsPaginated(pageRequest, userId);
return ResponseEntity.ok(transactions);
}

@GetMapping("/{id}")
@Operation(summary = "Get transaction by ID")
public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
Transaction transaction = transactionService.getTransactionById(id);
return ResponseEntity.ok(transaction);
}

@GetMapping("/code/{transactionCode}")
@Operation(summary = "Get transaction by code")
public ResponseEntity<Transaction> getTransactionByCode(@PathVariable String transactionCode) {
Transaction transaction = transactionService.getTransactionByCode(transactionCode);
return ResponseEntity.ok(transaction);
}

@GetMapping("/buyer/{buyerId}")
@Operation(summary = "Get transactions by buyer")
public ResponseEntity<List<Transaction>> getTransactionsByBuyer(@PathVariable Long buyerId) {
List<Transaction> transactions = transactionService.getTransactionsByBuyer(buyerId);
return ResponseEntity.ok(transactions);
}

@GetMapping("/seller/{sellerId}")
@Operation(summary = "Get transactions by seller")
public ResponseEntity<List<Transaction>> getTransactionsBySeller(@PathVariable Long sellerId) {
log.info("🌐 API Request: GET /api/transactions/seller/{} - Fetching seller transactions", sellerId);
List<Transaction> transactions = transactionService.getTransactionsBySeller(sellerId);
log.info("✅ API Response: Returning {} transactions to client for seller {}", transactions.size(), sellerId);
return ResponseEntity.ok(transactions);
}

@GetMapping("/my-transactions")
@Operation(summary = "Get current user's transactions (as seller/farmer)")
public ResponseEntity<List<Transaction>> getMyTransactions(jakarta.servlet.http.HttpServletRequest request) {
Long currentUserId = getCurrentUserId(request);
log.info("🌐 API Request: GET /api/transactions/my-transactions - Fetching transactions for user {}", currentUserId);
List<Transaction> transactions = transactionService.getTransactionsBySeller(currentUserId);
log.info("✅ API Response: Returning {} transactions to client for user {}", transactions.size(), currentUserId);
return ResponseEntity.ok(transactions);
}

private Long getCurrentUserId(jakarta.servlet.http.HttpServletRequest request) {
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

@GetMapping("/inventory/{inventoryId}")
@Operation(summary = "Get transactions by inventory")
public ResponseEntity<List<Transaction>> getTransactionsByInventory(
@PathVariable Long inventoryId,
jakarta.servlet.http.HttpServletRequest request) {
Long userId = null;
try {
userId = getCurrentUserId(request);
} catch (Exception e) {

log.debug("No authenticated user for transaction filtering: {}", e.getMessage());
}
List<Transaction> transactions = transactionService.getTransactionsByInventoryForUser(inventoryId, userId);
return ResponseEntity.ok(transactions);
}

@GetMapping("/payment-status/{paymentStatus}")
@Operation(summary = "Get transactions by payment status")
public ResponseEntity<List<Transaction>> getTransactionsByPaymentStatus(
@PathVariable PaymentStatus paymentStatus,
jakarta.servlet.http.HttpServletRequest request) {
Long userId = null;
try {
userId = getCurrentUserId(request);
} catch (Exception e) {

log.debug("No authenticated user for transaction filtering: {}", e.getMessage());
}
List<Transaction> transactions = transactionService.getTransactionsByPaymentStatusForUser(paymentStatus, userId);
return ResponseEntity.ok(transactions);
}

@GetMapping("/delivery-status/{deliveryStatus}")
@Operation(summary = "Get transactions by delivery status")
public ResponseEntity<List<Transaction>> getTransactionsByDeliveryStatus(
@PathVariable DeliveryStatus deliveryStatus,
jakarta.servlet.http.HttpServletRequest request) {
Long userId = null;
try {
userId = getCurrentUserId(request);
} catch (Exception e) {

log.debug("No authenticated user for transaction filtering: {}", e.getMessage());
}
List<Transaction> transactions = transactionService.getTransactionsByDeliveryStatusForUser(deliveryStatus, userId);
return ResponseEntity.ok(transactions);
}



@PatchMapping("/{id}/payment-status")
@Operation(summary = "Update payment status")
public ResponseEntity<Transaction> updatePaymentStatus(
@PathVariable Long id,
@RequestParam PaymentStatus paymentStatus) {
Transaction updatedTransaction = transactionService.updatePaymentStatus(id, paymentStatus);
return ResponseEntity.ok(updatedTransaction);
}

@PatchMapping("/{id}/delivery-status")
@Operation(summary = "Update delivery status")
public ResponseEntity<Transaction> updateDeliveryStatus(
@PathVariable Long id,
@RequestParam DeliveryStatus deliveryStatus,
jakarta.servlet.http.HttpServletRequest request) {
Long userId = null;
try {
userId = getCurrentUserId(request);
} catch (Exception e) {

log.debug("No authenticated user for delivery status update: {}", e.getMessage());
}
Transaction updatedTransaction = transactionService.updateDeliveryStatus(id, deliveryStatus, userId);
return ResponseEntity.ok(updatedTransaction);
}



@GetMapping("/exists/code/{transactionCode}")
@Operation(summary = "Check if transaction code exists")
public ResponseEntity<Boolean> transactionCodeExists(@PathVariable String transactionCode) {
boolean exists = transactionService.transactionCodeExists(transactionCode);
return ResponseEntity.ok(exists);
}

@GetMapping("/count")
@Operation(summary = "Get total number of transactions")
public ResponseEntity<Long> getTotalTransactions() {
long count = transactionService.getTotalTransactions();
return ResponseEntity.ok(count);
}

@GetMapping("/count/buyer/{buyerId}")
@Operation(summary = "Count transactions by buyer")
public ResponseEntity<Long> countTransactionsByBuyer(@PathVariable Long buyerId) {
long count = transactionService.countTransactionsByBuyer(buyerId);
return ResponseEntity.ok(count);
}

@GetMapping("/count/seller/{sellerId}")
@Operation(summary = "Count transactions by seller")
public ResponseEntity<Long> countTransactionsBySeller(@PathVariable Long sellerId) {
long count = transactionService.countTransactionsBySeller(sellerId);
return ResponseEntity.ok(count);
}

@GetMapping("/{id}/invoice")
@Operation(summary = "Export invoice as HTML")
public ResponseEntity<String> exportInvoice(@PathVariable Long id) {
Transaction transaction = transactionService.getTransactionById(id);
String invoiceHtml = invoiceService.generateInvoiceHtml(transaction);

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.TEXT_HTML);
headers.setContentDispositionFormData("attachment", "invoice-" + transaction.getTransactionCode() + ".html");

return new ResponseEntity<>(invoiceHtml, headers, HttpStatus.OK);
}

@GetMapping("/commission/total")
@Operation(summary = "Get total system commission (Admin only)")
public ResponseEntity<BigDecimal> getTotalCommission() {
BigDecimal total = transactionService.getTotalSystemCommission();
return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
}
}

