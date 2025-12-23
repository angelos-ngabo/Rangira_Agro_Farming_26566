package com.raf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest {

private String cropImageUrl;

private BigDecimal quantityKg;

private BigDecimal remainingQuantityKg;

private BigDecimal desiredPricePerKg;
}

