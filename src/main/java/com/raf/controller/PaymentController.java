package com.raf.controller;

import com.raf.dto.PaymentRequest;
import com.raf.entity.Payment;
import com.raf.entity.User;
import com.raf.service.PurchaseFlowService;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Payment processing APIs")
public class PaymentController {

private final PurchaseFlowService purchaseFlowService;
private final UserService userService;
private final JwtUtil jwtUtil;

@PostMapping
@Operation(summary = "Process payment (Buyer only)")
public ResponseEntity<Payment> processPayment(
@Valid @RequestBody PaymentRequest request,
HttpServletRequest httpRequest) {
Long buyerId = getCurrentUserId(httpRequest);
Payment payment = purchaseFlowService.processPayment(buyerId, request);
return new ResponseEntity<>(payment, HttpStatus.CREATED);
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
User user = userService.getUserByEmail(email);
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
}

