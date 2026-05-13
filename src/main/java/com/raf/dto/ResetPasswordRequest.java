package com.raf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used when a user is attempting to reset their password
 * using a recovery token sent to their email.
 */
@Data
public class ResetPasswordRequest {

/** The secure UUID token generated and emailed to the user for validation */
@NotBlank(message = "Reset token is required")
private String token;

/** The newly requested password to overwrite the forgotten one */
@NotBlank(message = "New password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String newPassword;
}

