package com.raf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnquiryRequest {
@NotNull(message = "Inventory ID is required")
private Long inventoryId;

@NotNull(message = "Proposed quantity is required")
private BigDecimal proposedQuantityKg;

@NotNull(message = "Proposed price per kg is required")
private BigDecimal proposedPricePerKg;

private String message;
}

