package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction extends BaseEntity {
    
    @NotBlank(message = "Transaction code is required")
    @Column(name = "transaction_code", unique = true, nullable = false, length = 20)
    private String transactionCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    @NotNull(message = "Inventory is required")
    @JsonBackReference("inventory-transactions")
    @ToString.Exclude
    private Inventory inventory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull(message = "Buyer is required")
    @JsonBackReference("buyer-transactions")
    @ToString.Exclude
    private User buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull(message = "Seller is required")
    @JsonBackReference("seller-transactions")
    @ToString.Exclude
    private User seller;
    
    
    @Column(name = "quantity_kg", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "Quantity is required")
    private BigDecimal quantityKg;
    
    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;
    
    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "storage_fee", precision = 12, scale = 2)
    private BigDecimal storageFee = BigDecimal.ZERO;
    
    @Column(name = "transaction_fee", precision = 12, scale = 2)
    private BigDecimal transactionFee = BigDecimal.ZERO;
    
    @Column(name = "net_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal netAmount;
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    
    
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("transaction-ratings")
    @ToString.Exclude
    private List<Rating> ratings = new ArrayList<>();
}

