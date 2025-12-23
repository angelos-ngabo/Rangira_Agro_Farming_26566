package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "enquiry")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Enquiry extends BaseEntity {

@NotBlank(message = "Enquiry code is required")
@Column(name = "enquiry_code", unique = true, nullable = false, length = 20)
private String enquiryCode;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "buyer_id", nullable = false)
@NotNull(message = "Buyer is required")
@JsonBackReference("buyer-enquiries")
@ToString.Exclude
private User buyer;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "farmer_id", nullable = false)
@NotNull(message = "Farmer is required")
@JsonBackReference("farmer-enquiries")
@ToString.Exclude
private User farmer;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "inventory_id", nullable = false)
@NotNull(message = "Inventory is required")
@JsonBackReference("inventory-enquiries")
@ToString.Exclude
private Inventory inventory;

@Column(name = "proposed_quantity_kg", precision = 12, scale = 2, nullable = false)
@NotNull(message = "Proposed quantity is required")
private BigDecimal proposedQuantityKg;

@Column(name = "proposed_price_per_kg", precision = 12, scale = 2, nullable = false)
@NotNull(message = "Proposed price is required")
private BigDecimal proposedPricePerKg;

@Column(name = "proposed_total_amount", precision = 12, scale = 2, nullable = false)
@NotNull(message = "Proposed total amount is required")
private BigDecimal proposedTotalAmount;

@Column(name = "message", columnDefinition = "text")
private String message;

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 20)
private com.raf.enums.EnquiryStatus status = com.raf.enums.EnquiryStatus.PENDING;

@Column(name = "enquiry_date", nullable = false)
private LocalDateTime enquiryDate;

@Column(name = "response_date")
private LocalDateTime responseDate;

@Column(name = "response_message", columnDefinition = "text")
private String responseMessage;

@OneToOne(mappedBy = "enquiry", fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "buyer", "seller", "inventory", "enquiry", "payments", "notifications", "ratings"})
@ToString.Exclude
private Transaction transaction;


@JsonProperty("transactionId")
public Long getTransactionId() {
return transaction != null ? transaction.getId() : null;
}
}

