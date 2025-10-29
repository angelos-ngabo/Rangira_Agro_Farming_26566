package com.raf.Rangira.Agro.Farming.dto;

import com.raf.Rangira.Agro.Farming.enums.DeliveryStatus;
import com.raf.Rangira.Agro.Farming.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequest {
    @NotBlank(message = "Transaction code is required")
    private String transactionCode;

    @NotNull(message = "Inventory ID is required")
    private Long inventoryId;

    @NotNull(message = "Buyer ID is required")
    private Long buyerId;

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantityKg;

    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    private BigDecimal storageFee;
    private BigDecimal transactionFee;

    @NotNull(message = "Net amount is required")
    private BigDecimal netAmount;

    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;

    private LocalDateTime paymentDate;
    private String notes;
}

