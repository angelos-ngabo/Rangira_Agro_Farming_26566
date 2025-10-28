package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.entity.Province;
import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.enums.UserStatus;
import com.raf.Rangira.Agro.Farming.enums.UserType;
import com.raf.Rangira.Agro.Farming.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User REST Controller
 * IMPORTANT: Demonstrates User-Location Relationship Endpoints
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get users with pagination")
    public ResponseEntity<Page<User>> getUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("firstName"));
        Page<User> users = userService.getUsersPaginated(pageRequest);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/code/{userCode}")
    @Operation(summary = "Get user by user code")
    public ResponseEntity<User> getUserByCode(@PathVariable String userCode) {
        User user = userService.getUserByUserCode(userCode);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/type/{userType}")
    @Operation(summary = "Get users by type")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable UserType userType) {
        List<User> users = userService.getUsersByType(userType);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get user by phone number (URL-encoded)")
    public ResponseEntity<User> getUserByPhone(@PathVariable String phoneNumber) {
        User user = userService.getUserByPhone(phoneNumber);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/search/by-phone")
    @Operation(summary = "Get user by phone number using query parameter (easier for special characters)")
    public ResponseEntity<User> searchUserByPhone(@RequestParam String phone) {
        User user = userService.getUserByPhone(phone);
        return ResponseEntity.ok(user);
    }
    
    // ============================================
    // REQUIREMENT: User-Location Relationship Endpoints
    // ============================================
    
    @GetMapping("/by-province-code/{provinceCode}")
    @Operation(summary = "Get users by province code (REQUIREMENT)")
    public ResponseEntity<List<User>> getUsersByProvinceCode(@PathVariable String provinceCode) {
        List<User> users = userService.getUsersByProvinceCode(provinceCode);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/by-province-name/{provinceName}")
    @Operation(summary = "Get users by province name (REQUIREMENT)")
    public ResponseEntity<List<User>> getUsersByProvinceName(@PathVariable String provinceName) {
        List<User> users = userService.getUsersByProvinceName(provinceName);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}/province")
    @Operation(summary = "Get province from user (REQUIREMENT - reverse lookup)")
    public ResponseEntity<Province> getProvinceFromUser(@PathVariable Long id) {
        Province province = userService.getProvinceFromUser(id);
        return ResponseEntity.ok(province);
    }
    
    @GetMapping("/{id}/full-location")
    @Operation(summary = "Get full location details from user")
    public ResponseEntity<Map<String, String>> getFullLocationFromUser(@PathVariable Long id) {
        String fullLocation = userService.getFullLocationFromUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("userId", id.toString());
        response.put("fullLocation", fullLocation);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/location")
    @Operation(summary = "Get complete location hierarchy from user (Village → Cell → Sector → District → Province)")
    public ResponseEntity<Map<String, Object>> getCompleteLocationFromUser(@PathVariable Long id) {
        Map<String, Object> location = userService.getCompleteLocationFromUser(id);
        return ResponseEntity.ok(location);
    }
    
    @GetMapping("/by-province/{provinceCode}/type/{userType}")
    @Operation(summary = "Get users by province code and user type")
    public ResponseEntity<List<User>> getUsersByProvinceAndType(
            @PathVariable String provinceCode,
            @PathVariable UserType userType) {
        List<User> users = userService.getUsersByProvinceCodeAndUserType(provinceCode, userType);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/count/by-province/{provinceCode}")
    @Operation(summary = "Count users by province code")
    public ResponseEntity<Long> countUsersByProvinceCode(@PathVariable String provinceCode) {
        long count = userService.countUsersByProvinceCode(provinceCode);
        return ResponseEntity.ok(count);
    }
    
    // ============================================
    // Standard CRUD endpoints
    // ============================================
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        User updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Check if email exists")
    public ResponseEntity<Boolean> emailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get total number of users")
    public ResponseEntity<Long> getTotalUsers() {
        long count = userService.getTotalUsers();
        return ResponseEntity.ok(count);
    }
}

