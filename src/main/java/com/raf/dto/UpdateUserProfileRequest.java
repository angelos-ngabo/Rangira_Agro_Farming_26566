package com.raf.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

private String firstName;

private String lastName;

@Email(message = "Invalid email format")
private String email;

private String phoneNumber;
}

