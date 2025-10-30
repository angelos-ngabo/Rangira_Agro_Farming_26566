package com.raf.dto;

import com.raf.enums.AccessLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
}

