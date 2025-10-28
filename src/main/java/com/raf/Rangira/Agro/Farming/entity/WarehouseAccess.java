package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.Rangira.Agro.Farming.enums.AccessLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * WarehouseAccess Entity
 * MANY-TO-MANY junction table between User and StorageWarehouse
 */
@Entity
@Table(name = "warehouse_access", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "warehouse_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WarehouseAccess extends BaseEntity {
    
    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @JsonBackReference
    @ToString.Exclude
    private User user;
    
    // Many-to-One relationship with StorageWarehouse
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull(message = "Warehouse is required")
    @JsonBackReference
    @ToString.Exclude
    private StorageWarehouse warehouse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    @NotNull(message = "Access level is required")
    private AccessLevel accessLevel;
    
    @Column(name = "granted_date", nullable = false)
    @NotNull(message = "Granted date is required")
    private LocalDate grantedDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}

