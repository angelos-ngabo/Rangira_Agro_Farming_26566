package com.raf.controller;

import com.raf.dto.AuthResponse;
import com.raf.dto.ForgotPasswordRequest;
import com.raf.dto.LoginRequest;
import com.raf.dto.OtpVerificationRequest;
import com.raf.dto.RegisterRequest;
import com.raf.dto.ResendOtpRequest;
import com.raf.dto.ResetPasswordRequest;
import com.raf.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs for login and registration")
public class AuthController {

private final AuthService authService;

@PostMapping("/login")
@Operation(summary = "Login user", description = "Authenticate user with email and password. Email must be verified.")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
AuthResponse response = authService.login(request);
return ResponseEntity.ok(response);
}

@PostMapping("/register")
@Operation(summary = "Register new user", description = "Register a new user account. OTP will be sent to email for verification.")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
AuthResponse response = authService.register(request);
return new ResponseEntity<>(response, HttpStatus.CREATED);
}

@PostMapping("/verify-otp")
@Operation(summary = "Verify OTP and activate account", description = "Verify the OTP code sent to email and activate the user account")
public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
AuthResponse response = authService.verifyOtpAndActivate(request.getEmail(), request.getOtpCode());
return ResponseEntity.ok(response);
}

@PostMapping("/resend-otp")
@Operation(summary = "Resend OTP", description = "Resend OTP verification code to user's email")
public ResponseEntity<Map<String, String>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
authService.resendOtp(request.getEmail());
Map<String, String> response = new HashMap<>();
response.put("message", "OTP has been resent to your email address");
return ResponseEntity.ok(response);
}

@PostMapping("/forgot-password")
@Operation(summary = "Request password reset", description = "Send password reset link to user's email")
public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
authService.forgotPassword(request.getEmail());
Map<String, String> response = new HashMap<>();
response.put("message", "If an account exists with this email, a password reset link has been sent");
return ResponseEntity.ok(response);
}

@PostMapping("/reset-password")
@Operation(summary = "Reset password", description = "Reset user password using reset token")
public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
authService.resetPassword(request.getToken(), request.getNewPassword());
Map<String, String> response = new HashMap<>();
response.put("message", "Password has been reset successfully");
return ResponseEntity.ok(response);
}

@PostMapping("/send-2fa")
@Operation(summary = "Send 2FA code for login", description = "Send a 2FA verification code to user's email during login")
public ResponseEntity<Map<String, String>> send2FACode(@Valid @RequestBody ResendOtpRequest request) {
authService.send2FACode(request.getEmail());
Map<String, String> response = new HashMap<>();
response.put("message", "2FA code has been sent to your email address");
return ResponseEntity.ok(response);
}

@PostMapping("/verify-2fa")
@Operation(summary = "Verify 2FA and complete login", description = "Verify the 2FA code sent to email and complete the login process")
public ResponseEntity<AuthResponse> verify2FA(@Valid @RequestBody OtpVerificationRequest request) {
AuthResponse response = authService.verify2FAAndLogin(request.getEmail(), request.getOtpCode());
return ResponseEntity.ok(response);
}
}

