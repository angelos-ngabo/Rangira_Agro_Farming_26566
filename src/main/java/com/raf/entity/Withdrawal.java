package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.WithdrawalMethod;
import com.raf.enums.WithdrawalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Withdrawal extends BaseEntity {

@NotBlank(message = "Withdrawal code is required")
@Column(name = "withdrawal_code", unique = true, nullable = false, length = 20)
private String withdrawalCode;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "wallet_id", nullable = false)
@NotNull(message = "Wallet is required")
@JsonBackReference("wallet-withdrawals")
@ToString.Exclude
private Wallet wallet;

@Column(name = "amount", precision = 12, scale = 2, nullable = false)
@NotNull(message = "Amount is required")
private BigDecimal amount;

@Enumerated(EnumType.STRING)
@Column(name = "withdrawal_method", nullable = false, length = 20)
@NotNull(message = "Withdrawal method is required")
private WithdrawalMethod withdrawalMethod;

@Column(name = "account_number", nullable = false, length = 50)
@NotBlank(message = "Account number is required")
private String accountNumber;

@Column(name = "account_name", length = 100)
private String accountName;

@Column(name = "bank_name", length = 100)
private String bankName;

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 20)
private WithdrawalStatus status = WithdrawalStatus.PENDING;

@Column(name = "request_date", nullable = false)
private LocalDateTime requestDate;

@Column(name = "processed_date")
private LocalDateTime processedDate;

@Column(name = "processing_notes", columnDefinition = "text")
private String processingNotes;

@Column(name = "otp_code", length = 6)
private String otpCode;

@Column(name = "otp_expires_at")
private LocalDateTime otpExpiresAt;

@Column(name = "otp_verified")
private Boolean otpVerified = false;
}

