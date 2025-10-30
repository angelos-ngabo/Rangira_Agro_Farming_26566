package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.dto.UserRequest;
import com.raf.Rangira.Agro.Farming.entity.Location;
import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.entity.UserProfile;
import com.raf.Rangira.Agro.Farming.enums.UserStatus;
import com.raf.Rangira.Agro.Farming.enums.UserType;
import com.raf.Rangira.Agro.Farming.repository.LocationRepository;
import com.raf.Rangira.Agro.Farming.repository.UserProfileRepository;
import com.raf.Rangira.Agro.Farming.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final LocationRepository locationRepository;
    
    public User createUserFromRequest(UserRequest request) {
        log.info("Creating user from request: {} {}", request.getFirstName(), request.getLastName());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered: " + request.getPhoneNumber());
        }
        
        if (userRepository.existsByUserCode(request.getUserCode())) {
            throw new RuntimeException("User code already exists: " + request.getUserCode());
        }
        
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + request.getLocationId()));
        
        User user = new User();
        user.setUserCode(request.getUserCode());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword());
        user.setUserType(request.getUserType());
        user.setStatus(UserStatus.ACTIVE);
        user.setLocation(location);
        
        User savedUser = userRepository.save(user);
        
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setVerified(false);
        userProfileRepository.save(profile);
        
        return savedUser;
    }
    
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
        
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setVerified(false);
        userProfileRepository.save(profile);
        
        return savedUser;
    }
    
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
    
    public List<User> getUsersByLocationCode(String locationCode) {
        log.info("Getting users by location code: {}", locationCode);
        return userRepository.findByLocationCode(locationCode);
    }
    
    public List<User> getUsersByLocation(Long locationId) {
        log.info("Getting users by location id: {}", locationId);
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        return userRepository.findByLocationOrAnyParent(location);
    }
    
    public List<User> getUsersByProvinceName(String provinceName) {
        log.info("Getting users by province name: {}", provinceName);
        List<Location> provinces = locationRepository.findByNameContainingIgnoreCase(provinceName);
        
        if (provinces.isEmpty()) {
            throw new RuntimeException("Province not found with name: " + provinceName);
        }
        
        Location province = provinces.stream()
                .filter(loc -> loc.getLevel().name().equals("PROVINCE"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No province found with name: " + provinceName));
        
        return userRepository.findByLocationOrAnyParent(province);
    }
    
    public Location getProvinceFromUser(Long userId) {
        log.info("Getting province for user id: {}", userId);
        User user = getUserById(userId);
        Location currentLocation = user.getLocation();
        
        while (currentLocation != null && !currentLocation.getLevel().name().equals("PROVINCE")) {
            currentLocation = currentLocation.getParent();
        }
        
        if (currentLocation == null) {
            throw new RuntimeException("Province not found for user");
        }
        
        // Force initialization to avoid lazy loading proxy serialization error
        Location province = locationRepository.findById(currentLocation.getId())
                .orElseThrow(() -> new RuntimeException("Province not found"));
        
        return province;
    }
    
    public Location getLocationFromUser(Long userId) {
        log.info("Getting location for user id: {}", userId);
        User user = getUserById(userId);
        return user.getLocation();
    }
    
    public String getFullLocationHierarchy(Long userId) {
        User user = getUserById(userId);
        return user.getLocation().getFullHierarchy();
    }
    
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User user = getUserById(id);
        
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
        user.setLocation(userDetails.getLocation());
        
        return userRepository.save(user);
    }
    
    public User updateUserStatus(Long id, UserStatus status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
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
        return userRepository.findByUserTypeAndStatus(userType, status).size();
    }
}
