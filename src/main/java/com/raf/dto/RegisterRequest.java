package com.raf.dto;

import com.raf.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user registration.
 * Captures all essential user details required to create a new account
 * and enforces validation constraints to ensure data integrity before
 * processing the registration request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

/** Unique alphanumeric identifier assigned to the user */
@NotBlank(message = "User code is required")
private String userCode;

/** User's given name */
@NotBlank(message = "First name is required")
private String firstName;

/** User's family name */
@NotBlank(message = "Last name is required")
private String lastName;

/** Valid email address used for authentication and notifications */
@NotBlank(message = "Email is required")
@Email(message = "Invalid email format")
private String email;

/** Primary contact number */
@NotBlank(message = "Phone number is required")
private String phoneNumber;

/** Raw password, to be encoded before persistence */
@NotBlank(message = "Password is required")
private String password;

/** Role classification (e.g., FARMER, STOREKEEPER, ADMIN) */
@NotNull(message = "User type is required")
private UserType userType;

/** Reference to the geographical location of the user */
@NotNull(message = "Location ID is required")
private java.util.UUID locationId;
}

