package com.raf.controller;

import com.raf.dto.WithdrawalRequest;
import com.raf.entity.User;
import com.raf.entity.Wallet;
import com.raf.entity.Withdrawal;
import com.raf.service.UserService;
import com.raf.service.WalletService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet", description = "Wallet and withdrawal management APIs")
public class WalletController {

private final WalletService walletService;
private final UserService userService;
private final JwtUtil jwtUtil;

@GetMapping
@Operation(summary = "Get user wallet")
public ResponseEntity<Wallet> getWallet(HttpServletRequest request) {
Long userId = getCurrentUserId(request);
return ResponseEntity.ok(walletService.getWalletByUserId(userId));
}

@PostMapping("/withdraw")
@Operation(summary = "Request withdrawal (sends OTP to email)")
public ResponseEntity<Withdrawal> requestWithdrawal(
@Valid @RequestBody WithdrawalRequest request,
HttpServletRequest httpRequest) {
Long userId = getCurrentUserId(httpRequest);
Withdrawal withdrawal = walletService.requestWithdrawal(userId, request);
return new ResponseEntity<>(withdrawal, HttpStatus.CREATED);
}

@PostMapping("/withdraw/{withdrawalId}/verify-otp")
@Operation(summary = "Verify OTP and complete withdrawal")
public ResponseEntity<Withdrawal> verifyOtpAndCompleteWithdrawal(
@PathVariable Long withdrawalId,
@RequestBody WithdrawalRequest request,
HttpServletRequest httpRequest) {
Long userId = getCurrentUserId(httpRequest);

Withdrawal withdrawal = walletService.getWithdrawalsByUserId(userId).stream()
.filter(w -> w.getId().equals(withdrawalId))
.findFirst()
.orElseThrow(() -> new RuntimeException("Withdrawal not found or unauthorized"));

Withdrawal completed = walletService.verifyOtpAndCompleteWithdrawal(withdrawalId, request.getOtpCode());
return ResponseEntity.ok(completed);
}

@GetMapping("/withdrawals")
@Operation(summary = "Get user withdrawals")
public ResponseEntity<List<Withdrawal>> getWithdrawals(HttpServletRequest request) {
Long userId = getCurrentUserId(request);
return ResponseEntity.ok(walletService.getWithdrawalsByUserId(userId));
}

@GetMapping("/commission/total")
@Operation(summary = "Get total system commission (Admin only)")
public ResponseEntity<BigDecimal> getTotalCommission() {
return ResponseEntity.ok(walletService.getTotalSystemCommission());
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

