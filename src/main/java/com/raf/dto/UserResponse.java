package com.raf.dto;

import com.raf.entity.Location;
import com.raf.entity.UserProfile;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
private Long id;
private String userCode;
private String firstName;
private String lastName;
private String email;
private String phoneNumber;
private UserType userType;
private UserStatus status;
private Location location;
private UserProfile userProfile;
private String profilePictureUrl;
private Boolean notificationsEnabled;
private Boolean twoFactorEnabled;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
}

