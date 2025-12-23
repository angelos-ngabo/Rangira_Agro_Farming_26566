package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.AccessLevel;
import com.raf.enums.WarehouseAccessStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

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

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
@NotNull(message = "User is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "warehouseAccesses", "password", "location"})
@ToString.Exclude
private User user;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "warehouse_id", nullable = false)
@NotNull(message = "Warehouse is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "warehouseAccesses", "inventories", "supportedCropTypes", "location"})
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

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 20)
private WarehouseAccessStatus status = WarehouseAccessStatus.PENDING;

@Column(name = "requested_capacity_kg", precision = 12, scale = 2)
private java.math.BigDecimal requestedCapacityKg;

@Column(name = "notes", columnDefinition = "text")
private String notes;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "crop_type_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private CropType cropType;

@Column(name = "crop_quantity_kg", precision = 12, scale = 2)
private java.math.BigDecimal cropQuantityKg;

@Column(name = "quality_grade", length = 5)
private String qualityGrade;

@Column(name = "expected_storage_date")
private LocalDate expectedStorageDate;

@Column(name = "expected_withdrawal_date")
private LocalDate expectedWithdrawalDate;

@Column(name = "crop_notes", columnDefinition = "text")
private String cropNotes;

@Column(name = "crop_image_url", length = 500)
private String cropImageUrl;

@Column(name = "desired_price_per_kg", precision = 12, scale = 2)
private java.math.BigDecimal desiredPricePerKg;
}

