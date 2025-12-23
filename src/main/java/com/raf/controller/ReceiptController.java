package com.raf.controller;

import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.enums.UserType;
import com.raf.exception.UnauthorizedException;
import com.raf.service.ReceiptService;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Receipt", description = "Receipt management APIs for buyers")
public class ReceiptController {

private final ReceiptService receiptService;
private final UserService userService;
private final JwtUtil jwtUtil;

@GetMapping
@Operation(summary = "Get all receipts for current buyer", description = "Get all paid transactions (receipts) for the logged-in buyer")
public ResponseEntity<List<Transaction>> getBuyerReceipts(HttpServletRequest request) {
Long buyerId = getCurrentUserId(request);
List<Transaction> receipts = receiptService.getBuyerReceipts(buyerId);
return ResponseEntity.ok(receipts);
}

@GetMapping("/{transactionId}")
@Operation(summary = "Get receipt by transaction ID", description = "Get a specific receipt for a transaction")
public ResponseEntity<Transaction> getReceiptByTransactionId(
@PathVariable Long transactionId,
HttpServletRequest request) {
Long buyerId = getCurrentUserId(request);
Transaction receipt = receiptService.getReceiptByTransactionId(transactionId, buyerId);
return ResponseEntity.ok(receipt);
}

@GetMapping("/{transactionId}/download")
@Operation(summary = "Download receipt as PDF", description = "Download receipt as HTML (can be converted to PDF)")
public ResponseEntity<String> downloadReceipt(
@PathVariable Long transactionId,
HttpServletRequest request) {
Long buyerId = getCurrentUserId(request);
Transaction transaction = receiptService.getReceiptByTransactionId(transactionId, buyerId);
String receiptHtml = receiptService.generateReceiptHtml(transaction);

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.TEXT_HTML);
headers.setContentDispositionFormData("attachment", "receipt_" + transaction.getTransactionCode() + ".html");

return new ResponseEntity<>(receiptHtml, headers, HttpStatus.OK);
}



@GetMapping("/admin/{transactionId}")
@Operation(summary = "Get receipt by transaction ID (Admin only)", description = "Admin can view receipt for any transaction")
public ResponseEntity<Transaction> getReceiptByTransactionIdAdmin(
@PathVariable Long transactionId,
HttpServletRequest request) {
User currentUser = getCurrentUser(request);
if (currentUser.getUserType() != UserType.ADMIN) {
throw new UnauthorizedException("Only admins can access this endpoint");
}

Transaction receipt = receiptService.getTransactionForAdmin(transactionId);
return ResponseEntity.ok(receipt);
}

@GetMapping("/admin/{transactionId}/download")
@Operation(summary = "Download receipt as PDF (Admin only)", description = "Admin can download receipt for any transaction")
public ResponseEntity<String> downloadReceiptAdmin(
@PathVariable Long transactionId,
HttpServletRequest request) {
User currentUser = getCurrentUser(request);
if (currentUser.getUserType() != UserType.ADMIN) {
throw new UnauthorizedException("Only admins can access this endpoint");
}

Transaction transaction = receiptService.getTransactionForAdmin(transactionId);
String receiptHtml = receiptService.generateReceiptHtml(transaction);

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.TEXT_HTML);
headers.setContentDispositionFormData("attachment", "receipt_" + transaction.getTransactionCode() + ".html");

return new ResponseEntity<>(receiptHtml, headers, HttpStatus.OK);
}

private Long getCurrentUserId(HttpServletRequest request) {
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
} catch (NumberFormatException e) {
log.error("NumberFormatException while getting userId: {}", e.getMessage());
throw new RuntimeException("Invalid user ID format: " + e.getMessage(), e);
} catch (Exception e) {
log.error("Failed to get user ID from request: {}", e.getMessage(), e);
throw new RuntimeException("Failed to get user ID from request: " + e.getMessage(), e);
}
}

private User getCurrentUser(HttpServletRequest request) {
try {
String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
log.error("Authorization header missing or invalid");
throw new RuntimeException("Authorization header missing or invalid");
}

String jwt = authHeader.substring(7);
String email = jwtUtil.extractUsername(jwt);
if (email == null || email.isEmpty()) {
log.error("Unable to extract username from token");
throw new RuntimeException("Unable to extract username from token");
}

User user = userService.getUserByEmail(email);
if (user == null) {
log.error("User not found with email: {}", email);
throw new RuntimeException("User not found with email: " + email);
}

return user;
} catch (Exception e) {
log.error("Failed to get current user from request: {}", e.getMessage(), e);
throw new RuntimeException("Failed to get current user from request: " + e.getMessage(), e);
}
}
}

