package com.raf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsletterSubscription {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, unique = true, length = 100)
private String email;

@Column(nullable = false)
@Builder.Default
private Boolean isActive = true;

@Column(nullable = false)
private LocalDateTime subscribedAt;

@Column
private LocalDateTime unsubscribedAt;

@PrePersist
protected void onCreate() {
if (subscribedAt == null) {
subscribedAt = LocalDateTime.now();
}
}
}

