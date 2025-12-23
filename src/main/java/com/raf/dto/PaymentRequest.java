package com.raf.dto;

import com.raf.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
@NotNull(message = "Transaction ID is required")
private Long transactionId;

@NotNull(message = "Amount is required")
private BigDecimal amount;

@NotNull(message = "Payment method is required")
private PaymentMethod paymentMethod;

private String paymentReference;

private String notes;
}

