package com.raf.dto;

import com.raf.enums.InventoryRequestType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestCreateDto {

@NotNull(message = "Inventory ID is required")
private Long inventoryId;

@NotNull(message = "Request type is required")
private InventoryRequestType requestType;


private String newCropImageUrl;

private BigDecimal newPricePerKg;

private String newNotes;


private BigDecimal withdrawalQuantityKg;

private LocalDate withdrawalDate;

private String farmerNotes;
}

