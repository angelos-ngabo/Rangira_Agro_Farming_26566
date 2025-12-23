package com.raf.repository;

import com.raf.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

Optional<OtpVerification> findByEmail(String email);

Optional<OtpVerification> findByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);

Optional<OtpVerification> findByEmailAndOtpCodeAndVerifiedFalse(String email, String otpCode);

void deleteByEmail(String email);

void deleteByExpiresAtBefore(java.time.LocalDateTime now);
}

