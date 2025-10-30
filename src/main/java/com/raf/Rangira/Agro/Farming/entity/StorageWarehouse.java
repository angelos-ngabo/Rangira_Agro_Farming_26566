package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raf.Rangira.Agro.Farming.enums.WarehouseStatus;
import com.raf.Rangira.Agro.Farming.enums.WarehouseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StorageWarehouse extends BaseEntity {
    
    @NotBlank(message = "Warehouse code is required")
    @Column(name = "warehouse_code", unique = true, nullable = false, length = 20)
    private String warehouseCode;
    
    @NotBlank(message = "Warehouse name is required")
    @Column(name = "warehouse_name", nullable = false, length = 100)
    private String warehouseName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", nullable = false, length = 50)
    @NotNull(message = "Warehouse type is required")
    private WarehouseType warehouseType;
    
    @Column(name = "total_capacity_kg", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "Total capacity is required")
    private BigDecimal totalCapacityKg;
    
    @Column(name = "available_capacity_kg", precision = 12, scale = 2, nullable = false)
    private BigDecimal availableCapacityKg;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WarehouseStatus status = WarehouseStatus.ACTIVE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    @NotNull(message = "Location is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private Location location;
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("warehouse-accesses")
    @ToString.Exclude
    private List<WarehouseAccess> warehouseAccesses = new ArrayList<>();
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("warehouse-inventories")
    @ToString.Exclude
    private List<Inventory> inventories = new ArrayList<>();
}

