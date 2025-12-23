package com.raf.service;

import com.raf.dto.AuthResponse;
import com.raf.dto.LoginRequest;
import com.raf.dto.RegisterRequest;
import com.raf.dto.UserRequest;
import com.raf.entity.OtpVerification;
import com.raf.entity.PasswordResetToken;
import com.raf.entity.User;
import com.raf.entity.UserProfile;
import com.raf.enums.UserStatus;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.PasswordResetTokenRepository;
import com.raf.repository.UserProfileRepository;
import com.raf.service.EmailService;
import com.raf.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class AuthService implements UserDetailsService {

private final UserService userService;
private final JwtUtil jwtUtil;
private final AuthenticationManager authenticationManager;
private final OtpService otpService;
private final UserProfileRepository userProfileRepository;
private final PasswordResetTokenRepository passwordResetTokenRepository;
private final EmailService emailService;
private final PasswordEncoder passwordEncoder;

public AuthService(UserService userService, JwtUtil jwtUtil, @Lazy AuthenticationManager authenticationManager,
OtpService otpService, UserProfileRepository userProfileRepository,
PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService,
PasswordEncoder passwordEncoder) {
this.userService = userService;
this.jwtUtil = jwtUtil;
this.authenticationManager = authenticationManager;
this.otpService = otpService;
this.userProfileRepository = userProfileRepository;
this.passwordResetTokenRepository = passwordResetTokenRepository;
this.emailService = emailService;
this.passwordEncoder = passwordEncoder;
}

@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
try {
User user = userService.getUserByEmail(email);
return org.springframework.security.core.userdetails.User.builder()
.username(user.getEmail())
.password(user.getPassword())
.authorities("ROLE_" + user.getUserType().name())
.accountExpired(false)
.accountLocked(false)
.credentialsExpired(false)
.disabled(!user.getStatus().name().equals("ACTIVE"))
.build();
} catch (ResourceNotFoundException e) {
throw new UsernameNotFoundException("User not found with email: " + email);
}
}

public AuthResponse login(LoginRequest request) {
log.info("Attempting login for email: {}", request.getEmail());

try {

User user;
try {
user = userService.getUserByEmail(request.getEmail());
} catch (ResourceNotFoundException e) {
log.error("User not found with email: {}", request.getEmail());
throw new BadCredentialsException("Invalid email or password");
}



if (user.getStatus() == UserStatus.INACTIVE) {
log.warn("Login attempt for inactive user: {}", request.getEmail());
throw new BadCredentialsException("Your account is inactive. Please verify your email address before logging in.");
}



if (user.getUserProfile() != null && !user.getUserProfile().getVerified() && user.getStatus() == UserStatus.ACTIVE) {
log.info("Auto-verifying email for existing ACTIVE user: {}", request.getEmail());
user.getUserProfile().setVerified(true);
userProfileRepository.save(user.getUserProfile());
} else if (user.getUserProfile() == null && user.getStatus() == UserStatus.ACTIVE) {

log.info("Creating and verifying profile for existing ACTIVE user: {}", request.getEmail());
UserProfile profile = new UserProfile();
profile.setUser(user);
profile.setVerified(true);
userProfileRepository.save(profile);
}


try {
authenticationManager.authenticate(
new UsernamePasswordAuthenticationToken(
request.getEmail(),
request.getPassword()
)
);
} catch (BadCredentialsException e) {
log.error("Invalid password for email: {}", request.getEmail());
throw new BadCredentialsException("Invalid email or password");
}




boolean requires2FA = user.getTwoFactorEnabled() != null && user.getTwoFactorEnabled();

if (requires2FA) {

OtpVerification otpVerification = otpService.generateAndSendOtp(user.getEmail(), user.getFirstName(), true);
String otpCode = otpVerification.getOtpCode();



return AuthResponse.builder()
.token(null)
.tokenType("Bearer")
.userId(user.getId())
.email(user.getEmail())
.firstName(user.getFirstName())
.lastName(user.getLastName())
.userType(user.getUserType())
.profilePictureUrl(null)
.message("Please check your email for the 2FA verification code. If email failed, check backend logs for OTP: " + otpCode)
.requires2FA(true)
.devOtpCode(otpCode)
.build();
} else {

String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserType().name());

String profilePictureUrl = null;
if (user.getUserProfile() != null && user.getUserProfile().getProfilePictureUrl() != null) {
profilePictureUrl = user.getUserProfile().getProfilePictureUrl();
}

return AuthResponse.builder()
.token(token)
.tokenType("Bearer")
.userId(user.getId())
.email(user.getEmail())
.firstName(user.getFirstName())
.lastName(user.getLastName())
.userType(user.getUserType())
.profilePictureUrl(profilePictureUrl)
.message("Login successful")
.requires2FA(false)
.build();
}
} catch (BadCredentialsException e) {
log.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
throw e;
} catch (Exception e) {
log.error("Unexpected error during login for email: {}", request.getEmail(), e);
throw new BadCredentialsException("An error occurred during login. Please try again.");
}
}

public AuthResponse verify2FAAndLogin(String email, String otpCode) {
log.info("Verifying 2FA for login with email: {}", email);


otpService.verifyOtp(email, otpCode);


User user = userService.getUserByEmail(email);


if (user.getStatus() == UserStatus.INACTIVE ||
(user.getUserProfile() != null && !user.getUserProfile().getVerified())) {
throw new BadCredentialsException("Please verify your email address before logging in.");
}


String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserType().name());

String profilePictureUrl = null;
if (user.getUserProfile() != null && user.getUserProfile().getProfilePictureUrl() != null) {
profilePictureUrl = user.getUserProfile().getProfilePictureUrl();
}

return AuthResponse.builder()
.token(token)
.tokenType("Bearer")
.userId(user.getId())
.email(user.getEmail())
.firstName(user.getFirstName())
.lastName(user.getLastName())
.userType(user.getUserType())
.profilePictureUrl(profilePictureUrl)
.message("2FA verification successful. Login completed.")
.requires2FA(false)
.build();
}

public void send2FACode(String email) {
log.info("Sending 2FA code for login to email: {}", email);
User user = userService.getUserByEmail(email);
otpService.generateAndSendOtp(email, user.getFirstName(), true);
}

public AuthResponse register(RegisterRequest request) {
log.info("Registering new user with email: {}", request.getEmail());


if (userService.emailExists(request.getEmail())) {
throw new DuplicateResourceException("Email already registered: " + request.getEmail());
}

if (userService.phoneExists(request.getPhoneNumber())) {
throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
}


UserRequest userRequest = new UserRequest();
userRequest.setUserCode(request.getUserCode());
userRequest.setFirstName(request.getFirstName());
userRequest.setLastName(request.getLastName());
userRequest.setEmail(request.getEmail());
userRequest.setPhoneNumber(request.getPhoneNumber());
userRequest.setPassword(request.getPassword());
userRequest.setUserType(request.getUserType());
userRequest.setLocationId(request.getLocationId());


User user = userService.createUserFromRequest(userRequest);


user.setStatus(UserStatus.INACTIVE);
user = userService.updateUserStatus(user.getId(), UserStatus.INACTIVE);


UserProfile profile = user.getUserProfile();
if (profile != null) {
profile.setVerified(false);
userProfileRepository.save(profile);
}


otpService.generateAndSendOtp(user.getEmail(), user.getFirstName());

return AuthResponse.builder()
.token(null)
.tokenType("Bearer")
.userId(user.getId())
.email(user.getEmail())
.firstName(user.getFirstName())
.lastName(user.getLastName())
.userType(user.getUserType())
.profilePictureUrl(null)
.message("Registration successful. Please check your email for verification code.")
.build();
}

public AuthResponse verifyOtpAndActivate(String email, String otpCode) {
log.info("Verifying OTP for email: {}", email);


otpService.verifyOtp(email, otpCode);


User user = userService.getUserByEmail(email);


user.setStatus(UserStatus.ACTIVE);
user = userService.updateUserStatus(user.getId(), UserStatus.ACTIVE);


UserProfile profile = user.getUserProfile();
if (profile != null) {
profile.setVerified(true);
userProfileRepository.save(profile);
}


String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserType().name());

String profilePictureUrl = null;
if (user.getUserProfile() != null && user.getUserProfile().getProfilePictureUrl() != null) {
profilePictureUrl = user.getUserProfile().getProfilePictureUrl();
}

return AuthResponse.builder()
.token(token)
.tokenType("Bearer")
.userId(user.getId())
.email(user.getEmail())
.firstName(user.getFirstName())
.lastName(user.getLastName())
.userType(user.getUserType())
.profilePictureUrl(profilePictureUrl)
.message("Email verified successfully. Account activated.")
.build();
}

public void resendOtp(String email) {
log.info("Resending OTP for email: {}", email);
User user = userService.getUserByEmail(email);
otpService.resendOtp(email, user.getFirstName());
}

public void forgotPassword(String email) {
log.info("Processing password reset request for email: {}", email);

try {
User user = userService.getUserByEmail(email);


passwordResetTokenRepository.deleteByEmail(email);


String token = UUID.randomUUID().toString();
PasswordResetToken resetToken = PasswordResetToken.builder()
.token(token)
.email(email)
.expiresAt(LocalDateTime.now().plusHours(1))
.used(false)
.build();

passwordResetTokenRepository.save(resetToken);


emailService.sendPasswordResetEmail(email, token, user.getFirstName());

log.info("Password reset token generated and email sent to: {}", email);
} catch (ResourceNotFoundException e) {

log.warn("Password reset requested for non-existent email: {}", email);

}
}

public void resetPassword(String token, String newPassword) {
log.info("Processing password reset with token");

PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
.orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));

if (!resetToken.isValid()) {
throw new BadCredentialsException("Invalid or expired reset token");
}


User user = userService.getUserByEmail(resetToken.getEmail());


userService.updateUserPassword(user.getId(), newPassword);


resetToken.setUsed(true);
passwordResetTokenRepository.save(resetToken);

log.info("Password reset successful for user: {}", user.getEmail());
}
}

