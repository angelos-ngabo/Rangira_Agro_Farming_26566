package com.raf.dto;

import com.raf.enums.WarehouseStatus;
import com.raf.enums.WarehouseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseUpdateRequest {
@NotBlank(message = "Warehouse code is required")
private String warehouseCode;

@NotBlank(message = "Warehouse name is required")
private String warehouseName;

@NotNull(message = "Warehouse type is required")
private WarehouseType warehouseType;

@NotNull(message = "Total capacity is required")
private BigDecimal totalCapacityKg;

@NotNull(message = "Available capacity is required")
private BigDecimal availableCapacityKg;

@NotNull(message = "Status is required")
private WarehouseStatus status;

@NotNull(message = "Location ID is required")
private java.util.UUID locationId;
}

