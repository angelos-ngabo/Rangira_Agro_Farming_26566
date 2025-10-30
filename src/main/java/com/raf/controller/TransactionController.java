package com.raf.controller;

import com.raf.dto.TransactionRequest;
import com.raf.entity.Transaction;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.PaymentStatus;
import com.raf.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "Transaction management APIs")
public class TransactionController {
    private final TransactionService transactionService;
    
    @PostMapping
    @Operation(summary = "Create a new transaction - use TransactionRequest with IDs")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction createdTransaction = transactionService.createTransactionFromRequest(request);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get transactions with pagination")
    public ResponseEntity<Page<Transaction>> getTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionService.getTransactionsPaginated(pageRequest);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/code/{transactionCode}")
    @Operation(summary = "Get transaction by code")
    public ResponseEntity<Transaction> getTransactionByCode(@PathVariable String transactionCode) {
        Transaction transaction = transactionService.getTransactionByCode(transactionCode);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/buyer/{buyerId}")
    @Operation(summary = "Get transactions by buyer")
    public ResponseEntity<List<Transaction>> getTransactionsByBuyer(@PathVariable Long buyerId) {
        List<Transaction> transactions = transactionService.getTransactionsByBuyer(buyerId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get transactions by seller")
    public ResponseEntity<List<Transaction>> getTransactionsBySeller(@PathVariable Long sellerId) {
        List<Transaction> transactions = transactionService.getTransactionsBySeller(sellerId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/inventory/{inventoryId}")
    @Operation(summary = "Get transactions by inventory")
    public ResponseEntity<List<Transaction>> getTransactionsByInventory(@PathVariable Long inventoryId) {
        List<Transaction> transactions = transactionService.getTransactionsByInventory(inventoryId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/payment-status/{paymentStatus}")
    @Operation(summary = "Get transactions by payment status")
    public ResponseEntity<List<Transaction>> getTransactionsByPaymentStatus(@PathVariable PaymentStatus paymentStatus) {
        List<Transaction> transactions = transactionService.getTransactionsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/delivery-status/{deliveryStatus}")
    @Operation(summary = "Get transactions by delivery status")
    public ResponseEntity<List<Transaction>> getTransactionsByDeliveryStatus(@PathVariable DeliveryStatus deliveryStatus) {
        List<Transaction> transactions = transactionService.getTransactionsByDeliveryStatus(deliveryStatus);
        return ResponseEntity.ok(transactions);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update transaction")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody Transaction transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(updatedTransaction);
    }
    
    @PatchMapping("/{id}/payment-status")
    @Operation(summary = "Update payment status")
    public ResponseEntity<Transaction> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus paymentStatus) {
        Transaction updatedTransaction = transactionService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(updatedTransaction);
    }
    
    @PatchMapping("/{id}/delivery-status")
    @Operation(summary = "Update delivery status")
    public ResponseEntity<Transaction> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam DeliveryStatus deliveryStatus) {
        Transaction updatedTransaction = transactionService.updateDeliveryStatus(id, deliveryStatus);
        return ResponseEntity.ok(updatedTransaction);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/exists/code/{transactionCode}")
    @Operation(summary = "Check if transaction code exists")
    public ResponseEntity<Boolean> transactionCodeExists(@PathVariable String transactionCode) {
        boolean exists = transactionService.transactionCodeExists(transactionCode);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get total number of transactions")
    public ResponseEntity<Long> getTotalTransactions() {
        long count = transactionService.getTotalTransactions();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/buyer/{buyerId}")
    @Operation(summary = "Count transactions by buyer")
    public ResponseEntity<Long> countTransactionsByBuyer(@PathVariable Long buyerId) {
        long count = transactionService.countTransactionsByBuyer(buyerId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/seller/{sellerId}")
    @Operation(summary = "Count transactions by seller")
    public ResponseEntity<Long> countTransactionsBySeller(@PathVariable Long sellerId) {
        long count = transactionService.countTransactionsBySeller(sellerId);
        return ResponseEntity.ok(count);
    }
}

