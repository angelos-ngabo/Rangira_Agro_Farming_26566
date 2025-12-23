package com.raf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StorageRequestDto {
@NotNull(message = "Warehouse ID is required")
private Long warehouseId;

@NotNull(message = "Crop type ID is required")
private Long cropTypeId;

@NotNull(message = "Quantity is required")
@Positive(message = "Quantity must be positive")
private BigDecimal quantityKg;

@NotBlank(message = "Quality grade is required")
private String qualityGrade;

private LocalDate expectedStorageDate;

private LocalDate expectedWithdrawalDate;

private String notes;
}

