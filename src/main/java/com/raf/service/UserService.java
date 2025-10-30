package com.raf.service;

import com.raf.dto.UserRequest;
import com.raf.entity.Location;
import com.raf.entity.User;
import com.raf.entity.UserProfile;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.LocationRepository;
import com.raf.repository.UserProfileRepository;
import com.raf.repository.UserRepository;
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
    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final LocationRepository locationRepository;

    public User createUserFromRequest(UserRequest request) {
        log.info("Creating user from request: {} {}", request.getFirstName(), request.getLastName());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
        }
        
        if (userRepository.existsByUserCode(request.getUserCode())) {
            throw new DuplicateResourceException("User code already exists: " + request.getUserCode());
        }
        
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));
        
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
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    
    public User getUserByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with code: " + userCode));
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
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return userRepository.findByLocationOrAnyParent(location);
    }
    
    public List<User> getUsersByProvinceName(String provinceName) {
        log.info("Getting users by province name: {}", provinceName);
        List<Location> provinces = locationRepository.findByNameContainingIgnoreCase(provinceName);
        
        if (provinces.isEmpty()) {
            throw new ResourceNotFoundException("Province not found with name: " + provinceName);
        }
        
        Location province = provinces.stream()
                .filter(loc -> loc.getLevel().name().equals("PROVINCE"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No province found with name: " + provinceName));
        
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
            throw new ResourceNotFoundException("Province not found for user");
        }
        
        return currentLocation;
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
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
        
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }
        
        if (!user.getPhoneNumber().equals(userDetails.getPhoneNumber()) && 
            userRepository.existsByPhoneNumber(userDetails.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already in use");
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
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
}
