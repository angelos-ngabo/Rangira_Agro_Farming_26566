package com.raf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false, unique = true, length = 100)
private String email;

@Column(nullable = false, length = 6)
private String otpCode;

@Column(nullable = false)
private LocalDateTime expiresAt;

@Column(nullable = false)
@Builder.Default
private Boolean verified = false;

@Column(nullable = false)
@Builder.Default
private Integer attempts = 0;

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
return !verified && !isExpired() && attempts < 5;
}
}

