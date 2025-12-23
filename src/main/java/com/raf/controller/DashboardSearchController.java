package com.raf.controller;

import com.raf.entity.User;
import com.raf.service.DashboardSearchService;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/search")
@RequiredArgsConstructor
@Tag(name = "Dashboard Search", description = "Dashboard search APIs with role-based access control")
@Slf4j
public class DashboardSearchController {

private final DashboardSearchService dashboardSearchService;
private final UserService userService;
private final JwtUtil jwtUtil;

@GetMapping
@Operation(summary = "Search dashboard data", description = "Search dashboard data with role-based restrictions")
public ResponseEntity<Map<String, Object>> search(
@RequestParam String q,
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size,
HttpServletRequest request) {

try {

Long userId = getCurrentUserId(request);
User user = userService.getUserById(userId);

if (user == null) {
log.error("User not found for ID: {}", userId);
return ResponseEntity.status(401).build();
}

Map<String, Object> results = dashboardSearchService.search(
q, userId, user.getUserType(), page, size);

return ResponseEntity.ok(results);
} catch (Exception e) {
log.error("Error in dashboard search: {}", e.getMessage(), e);
return ResponseEntity.status(401).build();
}
}

private Long getCurrentUserId(HttpServletRequest request) {
try {
String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
throw new RuntimeException("Authorization header missing or invalid");
}

String jwt = authHeader.substring(7);

try {
Long userId = jwtUtil.getUserIdFromToken(jwt);
if (userId != null) {
return userId;
}
} catch (Exception e) {
log.warn("Failed to extract userId from token: {}", e.getMessage());
}


String email = jwtUtil.extractUsername(jwt);
if (email == null || email.isEmpty()) {
throw new RuntimeException("Unable to extract username from token");
}

User user = userService.getUserByEmail(email);
if (user == null || user.getId() == null) {
throw new RuntimeException("User not found with email: " + email);
}

return user.getId();
} catch (Exception e) {
log.error("Error extracting current user ID: {}", e.getMessage());
throw new RuntimeException("Unable to extract current user ID", e);
}
}
}

