package com.raf.dto;

import com.raf.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
private String token;
@Builder.Default
private String tokenType = "Bearer";
private Long userId;
private String email;
private String firstName;
private String lastName;
private UserType userType;
private String profilePictureUrl;
private String message;
private Boolean requires2FA;

private String devOtpCode;
}

