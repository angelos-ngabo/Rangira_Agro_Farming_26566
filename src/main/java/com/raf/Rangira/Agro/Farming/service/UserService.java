package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.Province;
import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.entity.UserProfile;
import com.raf.Rangira.Agro.Farming.enums.UserStatus;
import com.raf.Rangira.Agro.Farming.enums.UserType;
import com.raf.Rangira.Agro.Farming.repository.UserProfileRepository;
import com.raf.Rangira.Agro.Farming.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Service
 * IMPORTANT: Demonstrates User-Location relationship
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    
    // CREATE
    public User createUser(User user) {
        log.info("Creating user: {} {}", user.getFirstName(), user.getLastName());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        if (userRepository.existsByUserCode(user.getUserCode())) {
            throw new RuntimeException("User code already exists");
        }
        
        // Hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // Create default user profile
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setVerified(false);
        userProfileRepository.save(profile);
        
        return savedUser;
    }
    
    // READ
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    
    public User getUserByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new RuntimeException("User not found with code: " + userCode));
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType);
    }
    
    public List<User> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    
    public User getUserByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phoneNumber));
    }
    
    public Page<User> getUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    // ============================================
    // REQUIREMENT: User-Location Relationship Methods
    // ============================================
    
    /**
     * Get users by province CODE (REQUIREMENT)
     */
    public List<User> getUsersByProvinceCode(String provinceCode) {
        log.info("Getting users by province code: {}", provinceCode);
        return userRepository.findByVillageCellSectorDistrictProvinceProvinceCode(provinceCode);
    }
    
    /**
     * Get users by province NAME (REQUIREMENT)
     */
    public List<User> getUsersByProvinceName(String provinceName) {
        log.info("Getting users by province name: {}", provinceName);
        return userRepository.findByVillageCellSectorDistrictProvinceProvinceName(provinceName);
    }
    
    /**
     * Get users by province ID
     */
    public List<User> getUsersByProvinceId(Long provinceId) {
        log.info("Getting users by province id: {}", provinceId);
        return userRepository.findByVillageCellSectorDistrictProvinceId(provinceId);
    }
    
    /**
     * Get province from user (REQUIREMENT - reverse lookup)
     */
    public Province getProvinceFromUser(Long userId) {
        log.info("Getting province for user id: {}", userId);
        User user = getUserById(userId);
        return user.getVillage().getCell().getSector().getDistrict().getProvince();
    }
    
    /**
     * Get full location details from user
     */
    public String getFullLocationFromUser(Long userId) {
        User user = getUserById(userId);
        Province province = user.getVillage().getCell().getSector().getDistrict().getProvince();
        String district = user.getVillage().getCell().getSector().getDistrict().getDistrictName();
        String sector = user.getVillage().getCell().getSector().getSectorName();
        String cell = user.getVillage().getCell().getCellName();
        String village = user.getVillage().getVillageName();
        
        return String.format("%s, %s, %s, %s, %s", 
            village, cell, sector, district, province.getProvinceName());
    }
    
    /**
     * Get complete location hierarchy from user
     */
    public Map<String, Object> getCompleteLocationFromUser(Long userId) {
        User user = getUserById(userId);
        Map<String, Object> location = new HashMap<>();
        
        location.put("village", Map.of(
            "id", user.getVillage().getId(),
            "code", user.getVillage().getVillageCode(),
            "name", user.getVillage().getVillageName()
        ));
        
        location.put("cell", Map.of(
            "id", user.getVillage().getCell().getId(),
            "code", user.getVillage().getCell().getCellCode(),
            "name", user.getVillage().getCell().getCellName()
        ));
        
        location.put("sector", Map.of(
            "id", user.getVillage().getCell().getSector().getId(),
            "code", user.getVillage().getCell().getSector().getSectorCode(),
            "name", user.getVillage().getCell().getSector().getSectorName()
        ));
        
        location.put("district", Map.of(
            "id", user.getVillage().getCell().getSector().getDistrict().getId(),
            "code", user.getVillage().getCell().getSector().getDistrict().getDistrictCode(),
            "name", user.getVillage().getCell().getSector().getDistrict().getDistrictName()
        ));
        
        location.put("province", Map.of(
            "id", user.getVillage().getCell().getSector().getDistrict().getProvince().getId(),
            "code", user.getVillage().getCell().getSector().getDistrict().getProvince().getProvinceCode(),
            "name", user.getVillage().getCell().getSector().getDistrict().getProvince().getProvinceName()
        ));
        
        return location;
    }
    
    /**
     * Get users by province and user type
     */
    public List<User> getUsersByProvinceCodeAndUserType(String provinceCode, UserType userType) {
        return userRepository.findUsersByProvinceCodeAndUserType(provinceCode, userType);
    }
    
    /**
     * Count users by province
     */
    public long countUsersByProvinceCode(String provinceCode) {
        return userRepository.countUsersByProvinceCode(provinceCode);
    }
    
    // UPDATE
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User user = getUserById(id);
        
        // Check for unique constraints
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        if (!user.getPhoneNumber().equals(userDetails.getPhoneNumber()) && 
            userRepository.existsByPhoneNumber(userDetails.getPhoneNumber())) {
            throw new RuntimeException("Phone number already in use");
        }
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setUserType(userDetails.getUserType());
        user.setVillage(userDetails.getVillage());
        
        return userRepository.save(user);
    }
    
    public User updateUserStatus(Long id, UserStatus status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    // DELETE
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    // Business Logic
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean phoneExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    public long getTotalUsersByTypeAndStatus(UserType userType, UserStatus status) {
        return userRepository.countByUserTypeAndStatus(userType, status);
    }
}

