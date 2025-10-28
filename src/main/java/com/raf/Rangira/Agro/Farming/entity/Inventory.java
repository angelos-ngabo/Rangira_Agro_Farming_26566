package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.raf.Rangira.Agro.Farming.enums.InventoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Entity
 * Core entity tracking stored crops with verification
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inventory extends BaseEntity {
    
    @NotBlank(message = "Inventory code is required")
    @Column(name = "inventory_code", unique = true, nullable = false, length = 20)
    private String inventoryCode;
    
    // Many-to-One: Farmer who owns the crop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    @NotNull(message = "Farmer is required")
    @JsonBackReference
    @ToString.Exclude
    private User farmer;
    
    // Many-to-One: Warehouse where crop is stored
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull(message = "Warehouse is required")
    @JsonBackReference
    @ToString.Exclude
    private StorageWarehouse warehouse;
    
    // Many-to-One: Type of crop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_type_id", nullable = false)
    @NotNull(message = "Crop type is required")
    @JsonBackReference
    @ToString.Exclude
    private CropType cropType;
    
    // Many-to-One: Storekeeper who logged the inventory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storekeeper_id", nullable = false)
    @NotNull(message = "Storekeeper is required")
    @JsonBackReference
    @ToString.Exclude
    private User storekeeper;
    
    @Column(name = "quantity_kg", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "Quantity is required")
    private BigDecimal quantityKg;
    
    @Column(name = "remaining_quantity_kg", precision = 12, scale = 2, nullable = false)
    private BigDecimal remainingQuantityKg;
    
    @Column(name = "quality_grade", nullable = false, length = 5)
    @NotBlank(message = "Quality grade is required")
    private String qualityGrade;
    
    @Column(name = "storage_date", nullable = false)
    @NotNull(message = "Storage date is required")
    private LocalDate storageDate;
    
    @Column(name = "expected_withdrawal_date")
    private LocalDate expectedWithdrawalDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InventoryStatus status = InventoryStatus.STORED;
    
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    
    // One-to-Many: Transactions for this inventory
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Transaction> transactions = new ArrayList<>();
}

