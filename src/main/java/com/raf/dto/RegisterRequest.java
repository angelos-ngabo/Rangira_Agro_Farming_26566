package com.raf.dto;

import com.raf.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique alphanumeric identifier assigned to the user", example = "USR-1001")
    @NotBlank(message = "User code is required")
    private String userCode;

    /** User's given name */
    @Schema(description = "User's given name", example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    /** User's family name */
    @Schema(description = "User's family name", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    /** Valid email address used for authentication and notifications */
    @Schema(description = "Valid email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** Primary contact number */
    @Schema(description = "Primary contact number", example = "0788000000")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    /** Raw password, to be encoded before persistence */
    @Schema(description = "Raw password", example = "Password@123")
    @NotBlank(message = "Password is required")
    private String password;

    /** Role classification (e.g., FARMER, STOREKEEPER, ADMIN) */
    @Schema(description = "Role classification", example = "FARMER")
    @NotNull(message = "User type is required")
    private UserType userType;

    /** Reference to the geographical location of the user */
    @Schema(description = "Reference to the geographical location of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Location ID is required")
    private java.util.UUID locationId;
}

