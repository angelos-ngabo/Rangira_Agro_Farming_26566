package com.raf.controller;

import com.raf.entity.NewsletterSubscription;
import com.raf.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@Tag(name = "Newsletter", description = "Newsletter subscription APIs")
public class NewsletterController {

private final NewsletterService newsletterService;

@PostMapping("/subscribe")
@Operation(summary = "Subscribe to newsletter", description = "Subscribe an email to the newsletter")
public ResponseEntity<NewsletterSubscription> subscribe(@Valid @RequestBody SubscribeRequest request) {
NewsletterSubscription subscription = newsletterService.subscribe(request.getEmail());
return ResponseEntity.ok(subscription);
}

@PostMapping("/unsubscribe")
@Operation(summary = "Unsubscribe from newsletter", description = "Unsubscribe an email from the newsletter")
public ResponseEntity<String> unsubscribe(@Valid @RequestBody SubscribeRequest request) {
newsletterService.unsubscribe(request.getEmail());
return ResponseEntity.ok("Successfully unsubscribed from newsletter");
}

@Data
public static class SubscribeRequest {
@Email(message = "Invalid email format")
private String email;
}
}

