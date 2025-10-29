package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.dto.UserRequest;
import com.raf.Rangira.Agro.Farming.entity.Location;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new user - use UserRequest with locationId")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
        User createdUser = userService.createUserFromRequest(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get users with pagination")
    public ResponseEntity<Page<User>> getUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("firstName"));
        return ResponseEntity.ok(userService.getUsersPaginated(pageRequest));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    
    @GetMapping("/code/{userCode}")
    @Operation(summary = "Get user by user code")
    public ResponseEntity<User> getUserByCode(@PathVariable String userCode) {
        return ResponseEntity.ok(userService.getUserByUserCode(userCode));
    }
    
    @GetMapping("/type/{userType}")
    @Operation(summary = "Get users by type")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable UserType userType) {
        return ResponseEntity.ok(userService.getUsersByType(userType));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable UserStatus status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }
    
    @GetMapping("/location/code/{locationCode}")
    @Operation(summary = "Get users by location code")
    public ResponseEntity<List<User>> getUsersByLocationCode(@PathVariable String locationCode) {
        return ResponseEntity.ok(userService.getUsersByLocationCode(locationCode));
    }
    
    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get users by location (includes children)")
    public ResponseEntity<List<User>> getUsersByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(userService.getUsersByLocation(locationId));
    }
    
    @GetMapping("/{id}/location")
    @Operation(summary = "Get location from user")
    public ResponseEntity<Location> getLocationFromUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getLocationFromUser(id));
    }
    
    @GetMapping("/province/code/{provinceCode}")
    @Operation(summary = "Get users by province code")
    public ResponseEntity<List<User>> getUsersByProvinceCode(@PathVariable String provinceCode) {
        List<User> users = userService.getUsersByLocationCode(provinceCode);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/province/name/{provinceName}")
    @Operation(summary = "Get users by province name")
    public ResponseEntity<List<User>> getUsersByProvinceName(@PathVariable String provinceName) {
        List<User> users = userService.getUsersByProvinceName(provinceName);
        return ResponseEntity.ok(users);
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
    @Operation(summary = "Update user")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Long id, 
            @RequestParam UserStatus status) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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
}
