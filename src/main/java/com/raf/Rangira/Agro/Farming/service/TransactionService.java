package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.dto.TransactionRequest;
import com.raf.Rangira.Agro.Farming.entity.Inventory;
import com.raf.Rangira.Agro.Farming.entity.Transaction;
import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.enums.DeliveryStatus;
import com.raf.Rangira.Agro.Farming.enums.PaymentStatus;
import com.raf.Rangira.Agro.Farming.exception.DuplicateResourceException;
import com.raf.Rangira.Agro.Farming.exception.ResourceNotFoundException;
import com.raf.Rangira.Agro.Farming.repository.InventoryRepository;
import com.raf.Rangira.Agro.Farming.repository.TransactionRepository;
import com.raf.Rangira.Agro.Farming.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    
    /**
     * Create transaction from DTO request
     */
    public Transaction createTransactionFromRequest(TransactionRequest request) {
        log.info("Creating transaction from request: {}", request.getTransactionCode());
        
        if (transactionRepository.existsByTransactionCode(request.getTransactionCode())) {
            throw new DuplicateResourceException("Transaction with code " + request.getTransactionCode() + " already exists");
        }
        
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + request.getInventoryId()));
        
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + request.getBuyerId()));
        
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + request.getSellerId()));
        
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(request.getTransactionCode());
        transaction.setInventory(inventory);
        transaction.setBuyer(buyer);
        transaction.setSeller(seller);
        transaction.setQuantityKg(request.getQuantityKg());
        transaction.setUnitPrice(request.getUnitPrice());
        transaction.setTotalAmount(request.getTotalAmount());
        transaction.setStorageFee(request.getStorageFee() != null ? request.getStorageFee() : BigDecimal.ZERO);
        transaction.setTransactionFee(request.getTransactionFee() != null ? request.getTransactionFee() : BigDecimal.ZERO);
        transaction.setNetAmount(request.getNetAmount());
        transaction.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PENDING);
        transaction.setDeliveryStatus(request.getDeliveryStatus() != null ? request.getDeliveryStatus() : DeliveryStatus.PENDING);
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setPaymentDate(request.getPaymentDate());
        transaction.setNotes(request.getNotes());
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Create a new transaction
     */
    public Transaction createTransaction(Transaction transaction) {
        // Check if transaction code already exists
        if (transactionRepository.existsByTransactionCode(transaction.getTransactionCode())) {
            throw new DuplicateResourceException("Transaction with code " + transaction.getTransactionCode() + " already exists");
        }
        
        // Set transaction date if not provided
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        
        log.info("Creating new transaction: {}", transaction.getTransactionCode());
        return transactionRepository.save(transaction);
    }
    
    /**
     * Get all transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    /**
     * Get transactions with pagination
     */
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsPaginated(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }
    
    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
    }
    
    /**
     * Get transaction by code
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionByCode(String transactionCode) {
        return transactionRepository.findByTransactionCode(transactionCode)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with code: " + transactionCode));
    }
    
    /**
     * Get transactions by buyer
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByBuyer(Long buyerId) {
        return transactionRepository.findByBuyerId(buyerId);
    }
    
    /**
     * Get transactions by seller
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsBySeller(Long sellerId) {
        return transactionRepository.findBySellerId(sellerId);
    }
    
    /**
     * Get transactions by inventory
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByInventory(Long inventoryId) {
        return transactionRepository.findByInventoryId(inventoryId);
    }
    
    /**
     * Get transactions by payment status
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus) {
        return transactionRepository.findByPaymentStatus(paymentStatus);
    }
    
    /**
     * Get transactions by delivery status
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDeliveryStatus(DeliveryStatus deliveryStatus) {
        return transactionRepository.findByDeliveryStatus(deliveryStatus);
    }
    
    /**
     * Update transaction
     */
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);
        
        transaction.setQuantityKg(transactionDetails.getQuantityKg());
        transaction.setUnitPrice(transactionDetails.getUnitPrice());
        transaction.setTotalAmount(transactionDetails.getTotalAmount());
        transaction.setPaymentStatus(transactionDetails.getPaymentStatus());
        transaction.setDeliveryStatus(transactionDetails.getDeliveryStatus());
        transaction.setNotes(transactionDetails.getNotes());
        
        log.info("Updating transaction ID: {}", id);
        return transactionRepository.save(transaction);
    }
    
    /**
     * Update payment status
     */
    public Transaction updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Transaction transaction = getTransactionById(id);
        transaction.setPaymentStatus(paymentStatus);
        
        if (paymentStatus == PaymentStatus.PAID) {
            transaction.setPaymentDate(LocalDateTime.now());
        }
        
        log.info("Updating transaction ID {} payment status to: {}", id, paymentStatus);
        return transactionRepository.save(transaction);
    }
    
    /**
     * Update delivery status
     */
    public Transaction updateDeliveryStatus(Long id, DeliveryStatus deliveryStatus) {
        Transaction transaction = getTransactionById(id);
        transaction.setDeliveryStatus(deliveryStatus);
        
        log.info("Updating transaction ID {} delivery status to: {}", id, deliveryStatus);
        return transactionRepository.save(transaction);
    }
    
    /**
     * Delete transaction
     */
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        log.info("Deleting transaction ID: {}", id);
        transactionRepository.delete(transaction);
    }
    
    /**
     * Check if transaction code exists
     */
    @Transactional(readOnly = true)
    public boolean transactionCodeExists(String transactionCode) {
        return transactionRepository.existsByTransactionCode(transactionCode);
    }
    
    /**
     * Get total number of transactions
     */
    @Transactional(readOnly = true)
    public long getTotalTransactions() {
        return transactionRepository.count();
    }
    
    /**
     * Count transactions by buyer
     */
    @Transactional(readOnly = true)
    public long countTransactionsByBuyer(Long buyerId) {
        return getTransactionsByBuyer(buyerId).size();
    }
    
    /**
     * Count transactions by seller
     */
    @Transactional(readOnly = true)
    public long countTransactionsBySeller(Long sellerId) {
        return getTransactionsBySeller(sellerId).size();
    }
}

