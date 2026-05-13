package com.raf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user authentication requests.
 * Carries the credentials necessary to perform a login operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

/** The registered email address of the user */
@NotBlank(message = "Email is required")
private String email;

/** The raw password provided for authentication */
@NotBlank(message = "Password is required")
private String password;
}

