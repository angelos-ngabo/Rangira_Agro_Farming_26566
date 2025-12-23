package com.raf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, unique = true, length = 100)
private String token;

@Column(nullable = false, length = 100)
private String email;

@Column(nullable = false)
private LocalDateTime expiresAt;

@Column(nullable = false)
@Builder.Default
private Boolean used = false;

@Column(nullable = false)
private LocalDateTime createdAt;

@PrePersist
protected void onCreate() {
createdAt = LocalDateTime.now();
}

public boolean isExpired() {
return LocalDateTime.now().isAfter(expiresAt);
}

public boolean isValid() {
return !used && !isExpired();
}
}

