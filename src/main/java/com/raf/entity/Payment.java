package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.PaymentMethod;
import com.raf.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment extends BaseEntity {

@NotBlank(message = "Payment code is required")
@Column(name = "payment_code", unique = true, nullable = false, length = 20)
private String paymentCode;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "transaction_id", nullable = false)
@NotNull(message = "Transaction is required")
@JsonBackReference("transaction-payments")
@ToString.Exclude
private Transaction transaction;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "payer_id", nullable = false)
@NotNull(message = "Payer is required")
@JsonBackReference("payer-payments")
@ToString.Exclude
private User payer;

@Column(name = "amount", precision = 12, scale = 2, nullable = false)
@NotNull(message = "Amount is required")
private BigDecimal amount;

@Enumerated(EnumType.STRING)
@Column(name = "payment_method", nullable = false, length = 20)
@NotNull(message = "Payment method is required")
private PaymentMethod paymentMethod;

@Column(name = "payment_reference", length = 100)
private String paymentReference;

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 20)
private PaymentStatus status = PaymentStatus.PENDING;

@Column(name = "payment_date")
private LocalDateTime paymentDate;

@Column(name = "processed_date")
private LocalDateTime processedDate;

@Column(name = "notes", columnDefinition = "text")
private String notes;
}

