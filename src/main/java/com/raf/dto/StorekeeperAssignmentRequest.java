package com.raf.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StorekeeperAssignmentRequest {
@NotNull(message = "Storekeeper user ID is required")
private Long storekeeperId;

@NotNull(message = "Warehouse ID is required")
private Long warehouseId;
}

