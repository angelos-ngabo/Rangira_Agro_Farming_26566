package com.raf.controller;

import com.raf.dto.UserRequest;
import com.raf.dto.UserResponse;
import com.raf.entity.Location;
import com.raf.entity.User;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "User management APIs")
public class UserController {
private final UserService userService;
private final JwtUtil jwtUtil;

@PostMapping
@Operation(summary = "Create a new user - use UserRequest with locationId")
public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
User createdUser = userService.createUserFromRequest(request);
return new ResponseEntity<>(userService.convertToResponse(createdUser), HttpStatus.CREATED);
}

@PostMapping("/admin/create")
@Operation(summary = "Admin create user", description = "Admin only - Create FARMER or STOREKEEPER users. Buyers must register themselves.")
public ResponseEntity<UserResponse> createUserByAdmin(@Valid @RequestBody com.raf.dto.AdminUserRequest request) {
User createdUser = userService.createUserByAdmin(request);
return new ResponseEntity<>(userService.convertToResponse(createdUser), HttpStatus.CREATED);
}

@GetMapping
@Operation(summary = "Get all users")
public ResponseEntity<List<UserResponse>> getAllUsers() {
log.info("🌐 API Request: GET /api/users - Fetching all users");
List<User> users = userService.getAllUsers();
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
log.info("✅ API Response: Returning {} users to client", responses.size());
return ResponseEntity.ok(responses);
}

@GetMapping("/paginated")
@Operation(summary = "Get users with pagination")
public ResponseEntity<Page<UserResponse>> getUsersPaginated(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size) {
log.info("🌐 API Request: GET /api/users/paginated - Fetching users (page: {}, size: {})", page, size);
PageRequest pageRequest = PageRequest.of(page, size, Sort.by("firstName"));
Page<User> userPage = userService.getUsersPaginated(pageRequest);
Page<UserResponse> responsePage = userPage.map(userService::convertToResponse);
log.info("✅ API Response: Returning {} users to client (page {} of {}, total: {})",
responsePage.getNumberOfElements(),
responsePage.getNumber() + 1,
responsePage.getTotalPages(),
responsePage.getTotalElements());
return ResponseEntity.ok(responsePage);
}


@GetMapping("/email/{email}")
@Operation(summary = "Get user by email")
public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
User user = userService.getUserByEmail(email);
return ResponseEntity.ok(userService.convertToResponse(user));
}

@GetMapping("/code/{userCode}")
@Operation(summary = "Get user by user code")
public ResponseEntity<UserResponse> getUserByCode(@PathVariable String userCode) {
User user = userService.getUserByUserCode(userCode);
return ResponseEntity.ok(userService.convertToResponse(user));
}

@GetMapping("/type/{userType}")
@Operation(summary = "Get users by type")
public ResponseEntity<List<UserResponse>> getUsersByType(@PathVariable UserType userType) {
List<User> users = userService.getUsersByType(userType);
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
return ResponseEntity.ok(responses);
}

@GetMapping("/status/{status}")
@Operation(summary = "Get users by status")
public ResponseEntity<List<UserResponse>> getUsersByStatus(@PathVariable UserStatus status) {
List<User> users = userService.getUsersByStatus(status);
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
return ResponseEntity.ok(responses);
}

@GetMapping("/location/code/{locationCode}")
@Operation(summary = "Get users by location code")
public ResponseEntity<List<UserResponse>> getUsersByLocationCode(@PathVariable String locationCode) {
List<User> users = userService.getUsersByLocationCode(locationCode);
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
return ResponseEntity.ok(responses);
}

@GetMapping("/location/{locationId}")
@Operation(summary = "Get users by location (includes children)")
public ResponseEntity<List<UserResponse>> getUsersByLocation(@PathVariable java.util.UUID locationId) {
List<User> users = userService.getUsersByLocation(locationId);
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
return ResponseEntity.ok(responses);
}

@GetMapping("/province/code/{provinceCode}")
@Operation(summary = "Get users by province code (includes all users in districts, sectors, cells, and villages within the province)")
public ResponseEntity<List<UserResponse>> getUsersByProvinceCode(@PathVariable String provinceCode) {
List<User> users = userService.getUsersByProvinceCode(provinceCode);
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
return ResponseEntity.ok(responses);
}

@GetMapping("/province/name/{provinceName}")
@Operation(summary = "Get users by province name")
public ResponseEntity<List<UserResponse>> getUsersByProvinceName(@PathVariable String provinceName) {
List<User> users = userService.getUsersByProvinceName(provinceName);
List<UserResponse> responses = users.stream()
.map(userService::convertToResponse)
.toList();
return ResponseEntity.ok(responses);
}


@GetMapping("/exists/email/{email}")
@Operation(summary = "Check if email exists")
public ResponseEntity<Map<String, Boolean>> emailExists(@PathVariable String email) {
Map<String, Boolean> response = new HashMap<>();
response.put("exists", userService.emailExists(email));
return ResponseEntity.ok(response);
}

@GetMapping("/exists/phone/{phone}")
@Operation(summary = "Check if phone exists")
public ResponseEntity<Map<String, Boolean>> phoneExists(@PathVariable String phone) {
Map<String, Boolean> response = new HashMap<>();
response.put("exists", userService.phoneExists(phone));
return ResponseEntity.ok(response);
}

@GetMapping("/stats/total")
@Operation(summary = "Get total users count")
public ResponseEntity<Map<String, Long>> getTotalUsers() {
Map<String, Long> response = new HashMap<>();
response.put("totalUsers", userService.getTotalUsers());
return ResponseEntity.ok(response);
}


@GetMapping("/{id}")
@Operation(summary = "Get user by ID")
public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
User user = userService.getUserById(id);
return ResponseEntity.ok(userService.convertToResponse(user));
}

@GetMapping("/{id}/location")
@Operation(summary = "Get location from user")
public ResponseEntity<Location> getLocationFromUser(@PathVariable Long id) {
return ResponseEntity.ok(userService.getLocationFromUser(id));
}

@GetMapping("/{id}/province")
@Operation(summary = "Get province from user (navigates up hierarchy)")
public ResponseEntity<Location> getProvinceFromUser(@PathVariable Long id) {
Location province = userService.getProvinceFromUser(id);
return ResponseEntity.ok(province);
}

@GetMapping("/{id}/location/hierarchy")
@Operation(summary = "Get full location hierarchy from user")
public ResponseEntity<Map<String, String>> getFullLocationHierarchy(@PathVariable Long id) {
String hierarchy = userService.getFullLocationHierarchy(id);
Map<String, String> response = new HashMap<>();
response.put("userId", id.toString());
response.put("locationHierarchy", hierarchy);
return ResponseEntity.ok(response);
}


@PutMapping("/{id}")
@Operation(summary = "Update user (admin only - requires all fields)")
public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody com.raf.dto.UpdateUserRequest request) {
User updatedUser = userService.updateUser(id, request);
return ResponseEntity.ok(userService.convertToResponse(updatedUser));
}

@PatchMapping("/{id}/profile")
@Operation(summary = "Update user profile (partial update - only provided fields)")
public ResponseEntity<UserResponse> updateUserProfile(
@PathVariable Long id,
@RequestBody com.raf.dto.UpdateUserProfileRequest request) {
User updatedUser = userService.updateUserProfile(id, request);
return ResponseEntity.ok(userService.convertToResponse(updatedUser));
}

@PatchMapping("/{id}/status")
@Operation(summary = "Update user status")
public ResponseEntity<UserResponse> updateUserStatus(
@PathVariable Long id,
@RequestParam UserStatus status) {
User updatedUser = userService.updateUserStatus(id, status);
return ResponseEntity.ok(userService.convertToResponse(updatedUser));
}

@DeleteMapping("/{id}")
@Operation(summary = "Delete user")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
userService.deleteUser(id);
return ResponseEntity.noContent().build();
}

@PostMapping("/{id}/profile-picture")
@Operation(summary = "Upload profile picture", description = "Upload a profile picture for the user. Max size: 5MB. Supported formats: JPG, PNG, GIF")
public ResponseEntity<UserResponse> uploadProfilePicture(
@PathVariable Long id,
@RequestParam("file") MultipartFile file) {
UserResponse response = userService.updateProfilePicture(id, file);
return ResponseEntity.ok(response);
}

@DeleteMapping("/{id}/profile-picture")
@Operation(summary = "Delete profile picture", description = "Remove the user's profile picture")
public ResponseEntity<UserResponse> deleteProfilePicture(@PathVariable Long id) {
UserResponse response = userService.deleteProfilePicture(id);
return ResponseEntity.ok(response);
}

@PatchMapping("/me/password")
@Operation(summary = "Change password", description = "Change the current user's password. Requires current password for verification.")
public ResponseEntity<Map<String, String>> changePassword(
@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
@Valid @RequestBody com.raf.dto.ChangePasswordRequest request) {
try {
log.info("Password change request received for user: {}", userDetails != null ? userDetails.getUsername() : "unknown");

if (userDetails == null) {
log.error("UserDetails is null in changePassword endpoint");
throw new RuntimeException("Authentication required");
}
String email = userDetails.getUsername();
if (email == null || email.isEmpty()) {
log.error("Email is null or empty in changePassword endpoint");
throw new RuntimeException("User email not found");
}
userService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());
Map<String, String> response = new HashMap<>();
response.put("message", "Password has been changed successfully");
return ResponseEntity.ok(response);
} catch (Exception e) {
log.error("Error changing password: {}", e.getMessage(), e);
throw e;
}
}

@PatchMapping("/me/notification-preference")
@Operation(summary = "Update notification preference", description = "Enable or disable notifications for the current user")
public ResponseEntity<Map<String, String>> updateNotificationPreference(
HttpServletRequest request,
@RequestParam Boolean enabled) {
String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
throw new RuntimeException("Authorization header missing or invalid");
}
String jwt = authHeader.substring(7);
Long userId = jwtUtil.getUserIdFromToken(jwt);
if (userId == null) {
String email = jwtUtil.extractUsername(jwt);
User user = userService.getUserByEmail(email);
userId = user.getId();
}
userService.updateNotificationPreference(userId, enabled);
Map<String, String> response = new HashMap<>();
response.put("message", "Notification preference updated successfully");
return ResponseEntity.ok(response);
}

@PatchMapping("/me/two-factor-preference")
@Operation(summary = "Update two-factor authentication preference", description = "Enable or disable two-factor authentication for the current user")
public ResponseEntity<Map<String, String>> updateTwoFactorPreference(
HttpServletRequest request,
@RequestParam Boolean enabled) {
String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
throw new RuntimeException("Authorization header missing or invalid");
}
String jwt = authHeader.substring(7);
Long userId = jwtUtil.getUserIdFromToken(jwt);
if (userId == null) {
String email = jwtUtil.extractUsername(jwt);
User user = userService.getUserByEmail(email);
userId = user.getId();
}
userService.updateTwoFactorPreference(userId, enabled);
Map<String, String> response = new HashMap<>();
response.put("message", "Two-factor authentication preference updated successfully");
return ResponseEntity.ok(response);
}
}
