package com.raf.service;

import com.raf.entity.OtpVerification;
import com.raf.exception.InvalidOtpException;
import com.raf.exception.OtpExpiredException;
import com.raf.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OtpService {

private final OtpVerificationRepository otpRepository;
private final EmailService emailService;
private static final SecureRandom random = new SecureRandom();

@Value("${app.email.verification.expiration:86400000}")
private long expirationMillis;


public OtpVerification generateAndSendOtp(String email, String firstName) {
return generateAndSendOtp(email, firstName, false);
}


public OtpVerification generateAndSendOtp(String email, String firstName, boolean isLogin) {
log.info("Generating OTP for email: {}", email);


Optional<OtpVerification> existingOtpOpt = otpRepository.findByEmail(email);


String otpCode = generateOtpCode();


LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMillis / 1000);

OtpVerification otp;
if (existingOtpOpt.isPresent()) {

otp = existingOtpOpt.get();
otp.setOtpCode(otpCode);
otp.setExpiresAt(expiresAt);
otp.setVerified(false);
otp.setAttempts(0);
otp.setCreatedAt(LocalDateTime.now());
log.info("Updating existing OTP for email: {}", email);
} else {

otp = OtpVerification.builder()
.email(email)
.otpCode(otpCode)
.expiresAt(expiresAt)
.verified(false)
.attempts(0)
.build();
log.info("Creating new OTP for email: {}", email);
}


otp = otpRepository.save(otp);


try {
log.info("Attempting to send {} OTP email to: {}", isLogin ? "login" : "registration", email);
log.info("Using email configuration - Host: smtp.gmail.com, Port: 587");
if (isLogin) {
emailService.sendLoginOtpEmail(email, otpCode, firstName);
} else {
emailService.sendRegistrationOtpEmail(email, otpCode, firstName);
}
log.info("✅ {} OTP email sent successfully to: {}", isLogin ? "Login" : "Registration", email);

return otp;
} catch (Exception e) {
log.error("❌ CRITICAL: Failed to send OTP email to: {}", email, e);
log.error("Email error details: {}", e.getMessage());
log.error("Exception class: {}", e.getClass().getName());
if (e.getCause() != null) {
log.error("Root cause: {}", e.getCause().getMessage());
log.error("Root cause class: {}", e.getCause().getClass().getName());
}

log.error("Full stack trace:", e);


log.error("═══════════════════════════════════════════════════════════");
log.error("⚠️  EMAIL FAILED - OTP CODE FOR TESTING:");
log.error("   Email: {}", email);
log.error("   OTP Code: {}", otpCode);
log.error("   (This OTP is saved in database and can be used for login)");
log.error("═══════════════════════════════════════════════════════════");



otp.setOtpCode(otpCode);
return otp;
}
}


public String getCurrentOtpCode(String email) {
Optional<OtpVerification> otpOpt = otpRepository.findByEmailAndVerifiedFalseOrderByCreatedAtDesc(email);
return otpOpt.map(OtpVerification::getOtpCode).orElse(null);
}

public void verifyOtp(String email, String otpCode) {
log.info("Verifying OTP for email: {}", email);

Optional<OtpVerification> otpOpt = otpRepository.findByEmailAndOtpCodeAndVerifiedFalse(email, otpCode);

if (otpOpt.isEmpty()) {

Optional<OtpVerification> existingOtp = otpRepository.findByEmailAndVerifiedFalseOrderByCreatedAtDesc(email);
if (existingOtp.isPresent()) {
OtpVerification otp = existingOtp.get();
otp.setAttempts(otp.getAttempts() + 1);
otpRepository.save(otp);

if (otp.getAttempts() >= 5) {
throw new InvalidOtpException("Too many failed attempts. Please request a new OTP.");
}
}
throw new InvalidOtpException("Invalid OTP code. Please check and try again.");
}

OtpVerification otp = otpOpt.get();


if (otp.isExpired()) {
throw new OtpExpiredException("OTP has expired. Please request a new OTP.");
}


otp.setVerified(true);
otpRepository.save(otp);

log.info("OTP verified successfully for email: {}", email);
}

public void resendOtp(String email, String firstName) {
log.info("Resending OTP for email: {}", email);
generateAndSendOtp(email, firstName);
}

public boolean isOtpVerified(String email) {
Optional<OtpVerification> otpOpt = otpRepository.findByEmailAndVerifiedFalseOrderByCreatedAtDesc(email);
if (otpOpt.isEmpty()) {
return true;
}
return otpOpt.get().getVerified();
}

private String generateOtpCode() {

int otp = 100000 + random.nextInt(900000);
return String.valueOf(otp);
}

@Transactional
public void cleanupExpiredOtps() {
otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
log.info("Cleaned up expired OTPs");
}
}

