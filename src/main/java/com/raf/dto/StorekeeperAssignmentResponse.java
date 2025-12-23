package com.raf.dto;

import com.raf.entity.WarehouseAccess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorekeeperAssignmentResponse {
private WarehouseAccess warehouseAccess;
private boolean requiresConfirmation;
private String existingWarehouseName;
private String message;
}

