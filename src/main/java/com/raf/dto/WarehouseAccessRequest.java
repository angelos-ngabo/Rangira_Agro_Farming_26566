package com.raf.dto;

import com.raf.enums.AccessLevel;
import com.raf.enums.WarehouseAccessStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WarehouseAccessRequest {
@NotNull(message = "User ID is required")
private Long userId;

@NotNull(message = "Warehouse ID is required")
private Long warehouseId;

@NotNull(message = "Access level is required")
private AccessLevel accessLevel;

@NotNull(message = "Granted date is required")
private LocalDate grantedDate;

private LocalDate expiryDate;
private Boolean isActive = true;
private WarehouseAccessStatus status = WarehouseAccessStatus.PENDING;
private BigDecimal requestedCapacityKg;
private String notes;


private Long cropTypeId;
private BigDecimal cropQuantityKg;
private String qualityGrade;
private LocalDate expectedStorageDate;
private LocalDate expectedWithdrawalDate;
private String cropNotes;
private String cropImageUrl;
private BigDecimal desiredPricePerKg;
}

