package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.raf.enums.InventoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
@Column(name = "inventory_code", unique = true, nullable = false, length = 50)
private String inventoryCode;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "farmer_id", nullable = false)
@NotNull(message = "Farmer is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "warehouseAccesses", "notifications", "wallet", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "messagesSent", "messagesReceived"})
@ToString.Exclude
private User farmer;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "warehouse_id", nullable = false)
@NotNull(message = "Warehouse is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "warehouseAccesses", "inventories", "supportedCropTypes", "location"})
@ToString.Exclude
private StorageWarehouse warehouse;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "crop_type_id", nullable = false)
@NotNull(message = "Crop type is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "inventories", "interestedUsers"})
@ToString.Exclude
private CropType cropType;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "storekeeper_id", nullable = false)
@NotNull(message = "Storekeeper is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "warehouseAccesses", "notifications", "wallet", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "messagesSent", "messagesReceived"})
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

@Column(name = "crop_image_url", length = 500)
private String cropImageUrl;

@OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("inventory-transactions")
@ToString.Exclude
private List<Transaction> transactions = new ArrayList<>();

@OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private List<Enquiry> enquiries = new ArrayList<>();
}

