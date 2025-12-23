package com.raf.controller;

import com.raf.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Tag(name = "Contact", description = "Contact form APIs")
public class ContactController {

private final EmailService emailService;

@PostMapping("/send")
@Operation(summary = "Send contact form message", description = "Send a message from the contact form to the system email")
public ResponseEntity<String> sendContactMessage(@Valid @RequestBody ContactRequest request) {
emailService.sendContactFormEmail(
request.getName(),
request.getEmail(),
request.getSubject(),
request.getMessage()
);
return ResponseEntity.ok("Your message has been sent successfully. We will get back to you soon!");
}

@PostMapping
@Operation(summary = "Send contact form message (alternative endpoint)", description = "Send a message from the contact form to the system email")
public ResponseEntity<String> sendContactMessageAlt(@Valid @RequestBody ContactRequest request) {
emailService.sendContactFormEmail(
request.getName(),
request.getEmail(),
request.getSubject(),
request.getMessage()
);
return ResponseEntity.ok("Your message has been sent successfully. We will get back to you soon!");
}

@Data
public static class ContactRequest {
@NotBlank(message = "Name is required")
private String name;

@NotBlank(message = "Email is required")
@Email(message = "Invalid email format")
private String email;

@NotBlank(message = "Subject is required")
private String subject;

@NotBlank(message = "Message is required")
private String message;
}
}

