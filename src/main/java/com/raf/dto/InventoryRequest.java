package com.raf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

@NotBlank(message = "Inventory code is required")
private String inventoryCode;

@NotNull(message = "Farmer ID is required")
private Long farmerId;

@NotNull(message = "Warehouse ID is required")
private Long warehouseId;

@NotNull(message = "Crop type ID is required")
private Long cropTypeId;

@NotNull(message = "Storekeeper ID is required")
private Long storekeeperId;

@NotNull(message = "Quantity is required")
private BigDecimal quantityKg;

@NotBlank(message = "Quality grade is required")
private String qualityGrade;

@NotNull(message = "Storage date is required")
private LocalDate storageDate;

private LocalDate expectedWithdrawalDate;

private String notes;

private String cropImageUrl;

private BigDecimal desiredPricePerKg;
}

