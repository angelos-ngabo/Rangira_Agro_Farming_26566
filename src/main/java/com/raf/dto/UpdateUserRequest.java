package com.raf.dto;

import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

@NotBlank(message = "First name is required")
private String firstName;

@NotBlank(message = "Last name is required")
private String lastName;

@NotBlank(message = "Email is required")
@Email(message = "Invalid email format")
private String email;

@NotBlank(message = "Phone number is required")
private String phoneNumber;

@NotNull(message = "User type is required")
private UserType userType;

@NotNull(message = "Location ID is required")
private UUID locationId;

@NotNull(message = "Status is required")
private UserStatus status;
}

