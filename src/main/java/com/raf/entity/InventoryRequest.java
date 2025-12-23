package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.InventoryRequestStatus;
import com.raf.enums.InventoryRequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventoryRequest extends BaseEntity {

@NotBlank(message = "Request code is required")
@Column(name = "request_code", unique = true, nullable = false, length = 50)
private String requestCode;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "inventory_id", nullable = false)
@NotNull(message = "Inventory is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "transactions", "enquiries"})
@ToString.Exclude
private Inventory inventory;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "farmer_id", nullable = false)
@NotNull(message = "Farmer is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "warehouseAccesses", "notifications", "wallet", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "messagesSent", "messagesReceived"})
@ToString.Exclude
private User farmer;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "storekeeper_id", nullable = false)
@NotNull(message = "Storekeeper is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "warehouseAccesses", "notifications", "wallet", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "messagesSent", "messagesReceived"})
@ToString.Exclude
private User storekeeper;

@Enumerated(EnumType.STRING)
@Column(name = "request_type", nullable = false, length = 20)
@NotNull(message = "Request type is required")
private InventoryRequestType requestType;

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 20)
private InventoryRequestStatus status = InventoryRequestStatus.PENDING;


@Column(name = "new_crop_image_url", length = 500)
private String newCropImageUrl;

@Column(name = "new_price_per_kg", precision = 12, scale = 2)
private BigDecimal newPricePerKg;

@Column(name = "new_notes", columnDefinition = "text")
private String newNotes;


@Column(name = "withdrawal_quantity_kg", precision = 12, scale = 2)
private BigDecimal withdrawalQuantityKg;

@Column(name = "withdrawal_date")
private LocalDate withdrawalDate;

@Column(name = "farmer_notes", columnDefinition = "text")
private String farmerNotes;

@Column(name = "storekeeper_response", columnDefinition = "text")
private String storekeeperResponse;

@Column(name = "processed_date")
private java.time.LocalDateTime processedDate;
}

