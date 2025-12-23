package com.raf.service;

import com.raf.dto.AdminUserRequest;
import com.raf.dto.UserRequest;
import com.raf.dto.UserResponse;
import com.raf.entity.CropType;
import com.raf.entity.Location;
import com.raf.entity.User;
import com.raf.entity.UserProfile;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.exception.UnauthorizedException;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.LocationRepository;
import com.raf.repository.UserProfileRepository;
import com.raf.repository.UserRepository;
import com.raf.service.FileStorageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
private final CropTypeRepository cropTypeRepository;
private final PasswordEncoder passwordEncoder;
private final FileStorageService fileStorageService;
@Lazy
private final NotificationService notificationService;

@PersistenceContext
private EntityManager entityManager;

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

user.setPassword(passwordEncoder.encode(request.getPassword()));
user.setUserType(request.getUserType());

user.setStatus(UserStatus.INACTIVE);
user.setLocation(location);

User savedUser = userRepository.save(user);
entityManager.flush();

UserProfile profile = new UserProfile();
profile.setUser(savedUser);
profile.setVerified(false);
userProfileRepository.save(profile);
entityManager.flush();

log.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
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
log.info("📊 Fetching all users from database...");
List<User> users = userRepository.findAll();
log.info("✅ Successfully fetched {} users from database", users.size());
if (users.isEmpty()) {
log.warn("⚠️  WARNING: No users found in database. Database may be empty or users table is not populated.");
} else {
log.info("📋 Users found: {}", users.stream()
.map(u -> String.format("%s (%s - %s)", u.getFirstName() + " " + u.getLastName(), u.getUserType(), u.getEmail()))
.collect(java.util.stream.Collectors.joining(", ")));
}
return users;
}

public List<User> getUsersByType(UserType userType) {
return userRepository.findByUserType(userType);
}

public List<User> getUsersByType(UserType userType, Sort sort) {
return userRepository.findByUserType(userType, sort);
}

public Page<User> getUsersByType(UserType userType, Pageable pageable) {
return userRepository.findByUserType(userType, pageable);
}

public List<User> getUsersByStatus(UserStatus status) {
return userRepository.findByStatus(status);
}

public List<User> getUsersByStatus(UserStatus status, Sort sort) {
return userRepository.findByStatus(status, sort);
}

public List<User> getUsersByTypeAndStatus(UserType userType, UserStatus status, Sort sort) {
return userRepository.findByUserTypeAndStatus(userType, status, sort);
}

public Page<User> getUsersPaginated(Pageable pageable) {
log.info("📊 Fetching paginated users from database (page: {}, size: {})...", pageable.getPageNumber(), pageable.getPageSize());
Page<User> userPage = userRepository.findAll(pageable);
log.info("✅ Successfully fetched {} users from database (page {} of {}, total: {})",
userPage.getNumberOfElements(),
userPage.getNumber() + 1,
userPage.getTotalPages(),
userPage.getTotalElements());
if (userPage.isEmpty()) {
log.warn("⚠️  WARNING: No users found in database for page {}. Database may be empty or users table is not populated.", pageable.getPageNumber());
}
return userPage;
}

public List<User> getUsersByLocationCode(String locationCode) {
log.info("Getting users by location code: {}", locationCode);

Location location = locationRepository.findByCode(locationCode)
.orElseThrow(() -> new ResourceNotFoundException("Location not found with code: " + locationCode));

if (location.getType() == com.raf.enums.ELocation.Province) {

return userRepository.findUsersByProvinceCode(locationCode);
} else {

return userRepository.findByLocationCode(locationCode);
}
}

public List<User> getUsersByLocation(java.util.UUID locationId) {
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
.filter(loc -> loc.getType() == com.raf.enums.ELocation.Province)
.findFirst()
.orElseThrow(() -> new ResourceNotFoundException("No province found with name: " + provinceName));


return userRepository.findUsersInProvince(province);
}

public List<User> getUsersByProvinceCode(String provinceCode) {
log.info("Getting users by province code: {}", provinceCode);

Location province = locationRepository.findByCode(provinceCode)
.orElseThrow(() -> new ResourceNotFoundException("Location not found with code: " + provinceCode));

if (province.getType() != com.raf.enums.ELocation.Province) {
throw new ResourceNotFoundException("Location with code " + provinceCode + " is not a province");
}


return userRepository.findUsersByProvinceCode(provinceCode);
}

public Location getProvinceFromUser(Long userId) {
log.info("Getting province for user id: {}", userId);
User user = getUserById(userId);
Location currentLocation = user.getLocation();

while (currentLocation != null && currentLocation.getType() != com.raf.enums.ELocation.Province) {
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
Location location = user.getLocation();
StringBuilder hierarchy = new StringBuilder(location.getName());
Location current = location.getParent();
while (current != null) {
hierarchy.insert(0, current.getName() + " > ");
current = current.getParent();
}
return hierarchy.toString();
}

public User updateUser(Long id, com.raf.dto.UpdateUserRequest request) {
log.info("Updating user with id: {}", id);

User user = userRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

if (!user.getEmail().equals(request.getEmail()) &&
userRepository.existsByEmail(request.getEmail())) {
throw new DuplicateResourceException("Email already in use");
}

if (!user.getPhoneNumber().equals(request.getPhoneNumber()) &&
userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
throw new DuplicateResourceException("Phone number already in use");
}


Location location = locationRepository.findById(request.getLocationId())
.orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + request.getLocationId()));

user.setFirstName(request.getFirstName());
user.setLastName(request.getLastName());
user.setEmail(request.getEmail());
user.setPhoneNumber(request.getPhoneNumber());
user.setUserType(request.getUserType());
user.setStatus(request.getStatus());
user.setLocation(location);

User savedUser = userRepository.save(user);
entityManager.flush();


try {
notificationService.notifyAllAdmins(
"User Updated",
String.format("User '%s %s' (%s) has been updated.",
savedUser.getFirstName(),
savedUser.getLastName(),
savedUser.getEmail()),
com.raf.enums.NotificationType.USER_UPDATED,
"/users"
);
} catch (Exception e) {
log.error("Failed to send notification for user update: {}", e.getMessage());
}

return savedUser;
}


public User updateUserProfile(Long id, com.raf.dto.UpdateUserProfileRequest request) {
log.info("Updating user profile for id: {}", id);

User user = userRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));


if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
user.setFirstName(request.getFirstName().trim());
}

if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
user.setLastName(request.getLastName().trim());
}










if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
if (!user.getPhoneNumber().equals(request.getPhoneNumber()) &&
userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
throw new DuplicateResourceException("Phone number already in use");
}
user.setPhoneNumber(request.getPhoneNumber().trim());
}

User savedUser = userRepository.save(user);
entityManager.flush();

log.info("User profile updated successfully for id: {}", id);
return savedUser;
}

public User updateUserStatus(Long id, UserStatus status) {
User user = userRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
user.setStatus(status);
User savedUser = userRepository.save(user);
entityManager.flush();
return savedUser;
}

public User updateUserPassword(Long id, String newPassword) {
log.info("Updating password for user id: {}", id);
User user = userRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
user.setPassword(passwordEncoder.encode(newPassword));
User savedUser = userRepository.save(user);
entityManager.flush();
return savedUser;
}


public void changePassword(String email, String currentPassword, String newPassword) {
log.info("Changing password for user with email: {}", email);

User user = getUserByEmail(email);


if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
throw new UnauthorizedException("Current password is incorrect");
}


if (passwordEncoder.matches(newPassword, user.getPassword())) {
throw new IllegalArgumentException("New password must be different from current password");
}


user.setPassword(passwordEncoder.encode(newPassword));
userRepository.save(user);
entityManager.flush();

log.info("Password changed successfully for user: {}", email);
}

public void updateNotificationPreference(Long userId, Boolean enabled) {
log.info("Updating notification preference for user id: {} to: {}", userId, enabled);
User user = getUserById(userId);
user.setNotificationsEnabled(enabled != null ? enabled : true);
userRepository.save(user);
entityManager.flush();
log.info("Notification preference updated successfully for user id: {}", userId);
}

public void updateTwoFactorPreference(Long userId, Boolean enabled) {
log.info("Updating two-factor authentication preference for user id: {} to: {}", userId, enabled);
User user = getUserById(userId);
user.setTwoFactorEnabled(enabled != null ? enabled : false);
userRepository.save(user);
entityManager.flush();
log.info("Two-factor authentication preference updated successfully for user id: {}", userId);
}

public void deleteUser(Long id) {
log.info("Deleting user with id: {}", id);
User user = userRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
String userName = user.getFirstName() + " " + user.getLastName();
String userEmail = user.getEmail();
userRepository.delete(user);


try {
notificationService.notifyAllAdmins(
"User Deleted",
String.format("User '%s' (%s) has been deleted.", userName, userEmail),
com.raf.enums.NotificationType.USER_DELETED,
"/users"
);
} catch (Exception e) {
log.error("Failed to send notification for user deletion: {}", e.getMessage());
}
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

public UserResponse convertToResponse(User user) {
String profilePictureUrl = null;
if (user.getUserProfile() != null && user.getUserProfile().getProfilePictureUrl() != null) {
profilePictureUrl = user.getUserProfile().getProfilePictureUrl();
}

return UserResponse.builder()
.id(user.getId())
.userCode(user.getUserCode())
.firstName(user.getFirstName())
.lastName(user.getLastName())
.email(user.getEmail())
.phoneNumber(user.getPhoneNumber())
.userType(user.getUserType())
.status(user.getStatus())
.location(user.getLocation())
.userProfile(user.getUserProfile())
.profilePictureUrl(profilePictureUrl)
.notificationsEnabled(user.getNotificationsEnabled())
.twoFactorEnabled(user.getTwoFactorEnabled())
.createdAt(user.getCreatedAt())
.updatedAt(user.getUpdatedAt())
.build();
}

public UserResponse updateProfilePicture(Long userId, MultipartFile file) {
log.info("Updating profile picture for user id: {}", userId);

User user = getUserById(userId);
UserProfile profile = user.getUserProfile();

if (profile == null) {
profile = new UserProfile();
profile.setUser(user);
profile.setVerified(false);
profile = userProfileRepository.save(profile);
}


if (profile.getProfilePictureUrl() != null) {
String oldFilename = extractFilenameFromUrl(profile.getProfilePictureUrl());
if (oldFilename != null) {
fileStorageService.deleteProfilePicture(oldFilename);
}
}


String fileUrl = fileStorageService.storeProfilePicture(file, userId);
profile.setProfilePictureUrl(fileUrl);
userProfileRepository.save(profile);

log.info("Profile picture updated for user id: {}", userId);
return convertToResponse(user);
}

public UserResponse deleteProfilePicture(Long userId) {
log.info("Deleting profile picture for user id: {}", userId);

User user = getUserById(userId);
UserProfile profile = user.getUserProfile();

if (profile != null && profile.getProfilePictureUrl() != null) {
String filename = extractFilenameFromUrl(profile.getProfilePictureUrl());
if (filename != null) {
fileStorageService.deleteProfilePicture(filename);
}
profile.setProfilePictureUrl(null);
userProfileRepository.save(profile);
}

log.info("Profile picture deleted for user id: {}", userId);
return convertToResponse(user);
}

private String extractFilenameFromUrl(String url) {
if (url == null || url.isEmpty()) {
return null;
}

int lastSlash = url.lastIndexOf('/');
if (lastSlash >= 0 && lastSlash < url.length() - 1) {
return url.substring(lastSlash + 1);
}
return null;
}


public User createUserByAdmin(AdminUserRequest request) {
log.info("Admin creating user: {} {} ({})", request.getFirstName(), request.getLastName(), request.getUserType());


if (request.getUserType() == UserType.BUYER) {
throw new UnauthorizedException("Admin cannot create BUYER users. Buyers must register themselves.");
}

if (request.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Admin cannot create other ADMIN users through this endpoint.");
}


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
user.setPassword(passwordEncoder.encode(request.getPassword()));
user.setUserType(request.getUserType());
user.setStatus(UserStatus.ACTIVE);
user.setLocation(location);

User savedUser = userRepository.save(user);
entityManager.flush();


UserProfile profile = new UserProfile();
profile.setUser(savedUser);
profile.setVerified(true);
userProfileRepository.save(profile);
entityManager.flush();


if (request.getUserType() == UserType.FARMER && request.getCropTypeIds() != null && !request.getCropTypeIds().isEmpty()) {
List<CropType> cropTypes = cropTypeRepository.findAllById(request.getCropTypeIds());
savedUser.setInterestedCropTypes(cropTypes);
userRepository.save(savedUser);
entityManager.flush();
log.info("Added {} crop interests for farmer {}", cropTypes.size(), savedUser.getEmail());
}

log.info("Admin successfully created {} user with ID: {} and email: {}", request.getUserType(), savedUser.getId(), savedUser.getEmail());
return savedUser;
}
}
