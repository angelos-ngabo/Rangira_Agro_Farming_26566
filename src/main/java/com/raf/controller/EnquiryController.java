package com.raf.controller;

import com.raf.dto.EnquiryRequest;
import com.raf.dto.EnquiryResponseRequest;
import com.raf.entity.Enquiry;
import com.raf.entity.User;
import com.raf.service.EnquiryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enquiry", description = "Buyer-farmer enquiry management APIs")
public class EnquiryController {

private final EnquiryService enquiryService;
private final PurchaseFlowService purchaseFlowService;
private final UserService userService;
private final JwtUtil jwtUtil;

@PostMapping
@Operation(summary = "Create enquiry (Buyer only)")
public ResponseEntity<Enquiry> createEnquiry(
@Valid @RequestBody EnquiryRequest request,
HttpServletRequest httpRequest) {

Long buyerId = getCurrentUserId(httpRequest);
Enquiry enquiry = enquiryService.createEnquiry(buyerId, request);
return new ResponseEntity<>(enquiry, HttpStatus.CREATED);
}

@PostMapping("/{id}/respond")
@Operation(summary = "Respond to enquiry (Farmer only)")
public ResponseEntity<Enquiry> respondToEnquiry(
@PathVariable Long id,
@Valid @RequestBody EnquiryResponseRequest request,
HttpServletRequest httpRequest) {
Long farmerId = getCurrentUserId(httpRequest);


request.setEnquiryId(id);

Enquiry enquiry = enquiryService.respondToEnquiry(farmerId, request);




return ResponseEntity.ok(enquiry);
}

@GetMapping("/buyer")
@Operation(summary = "Get buyer's enquiries")
public ResponseEntity<List<Enquiry>> getBuyerEnquiries(HttpServletRequest request) {
Long buyerId = getCurrentUserId(request);
return ResponseEntity.ok(enquiryService.getEnquiriesByBuyer(buyerId));
}

@GetMapping("/farmer")
@Operation(summary = "Get farmer's enquiries")
public ResponseEntity<List<Enquiry>> getFarmerEnquiries(HttpServletRequest request) {
Long farmerId = getCurrentUserId(request);
return ResponseEntity.ok(enquiryService.getEnquiriesByFarmer(farmerId));
}

@GetMapping("/{id}")
@Operation(summary = "Get enquiry by ID")
public ResponseEntity<Enquiry> getEnquiryById(@PathVariable Long id) {
return ResponseEntity.ok(enquiryService.getEnquiryById(id));
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

