package com.raf.dto;

import com.raf.enums.WarehouseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WarehouseCreateRequest {
@NotBlank(message = "Warehouse code is required")
private String warehouseCode;

@NotBlank(message = "Warehouse name is required")
private String warehouseName;

@NotNull(message = "Warehouse type is required")
private WarehouseType warehouseType;

@NotNull(message = "Total capacity is required")
private BigDecimal totalCapacityKg;

@NotNull(message = "Location ID is required")
private java.util.UUID locationId;

@NotNull(message = "At least one crop type must be specified")
@Size(min = 1, message = "At least one crop type must be specified")
private List<Long> cropTypeIds;
}

