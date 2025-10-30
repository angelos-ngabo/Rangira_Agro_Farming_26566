package com.raf.service;

import com.raf.dto.TransactionRequest;
import com.raf.entity.Inventory;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.PaymentStatus;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.InventoryRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

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

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsPaginated(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Transaction getTransactionByCode(String transactionCode) {
        return transactionRepository.findByTransactionCode(transactionCode)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with code: " + transactionCode));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByBuyer(Long buyerId) {
        return transactionRepository.findByBuyerId(buyerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsBySeller(Long sellerId) {
        return transactionRepository.findBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByInventory(Long inventoryId) {
        return transactionRepository.findByInventoryId(inventoryId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus) {
        return transactionRepository.findByPaymentStatus(paymentStatus);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDeliveryStatus(DeliveryStatus deliveryStatus) {
        return transactionRepository.findByDeliveryStatus(deliveryStatus);
    }

    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        
        transaction.setQuantityKg(transactionDetails.getQuantityKg());
        transaction.setUnitPrice(transactionDetails.getUnitPrice());
        transaction.setTotalAmount(transactionDetails.getTotalAmount());
        transaction.setPaymentStatus(transactionDetails.getPaymentStatus());
        transaction.setDeliveryStatus(transactionDetails.getDeliveryStatus());
        transaction.setNotes(transactionDetails.getNotes());
        
        log.info("Updating transaction ID: {}", id);
        return transactionRepository.save(transaction);
    }

    public Transaction updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        transaction.setPaymentStatus(paymentStatus);

        if (paymentStatus == PaymentStatus.PAID) {
            transaction.setPaymentDate(LocalDateTime.now());
        }

        log.info("Updating transaction ID {} payment status to: {}", id, paymentStatus);
        return transactionRepository.save(transaction);
    }

    public Transaction updateDeliveryStatus(Long id, DeliveryStatus deliveryStatus) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        transaction.setDeliveryStatus(deliveryStatus);

        log.info("Updating transaction ID {} delivery status to: {}", id, deliveryStatus);
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        log.info("Deleting transaction ID: {}", id);
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public boolean transactionCodeExists(String transactionCode) {
        return transactionRepository.existsByTransactionCode(transactionCode);
    }

    @Transactional(readOnly = true)
    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    @Transactional(readOnly = true)
    public long countTransactionsByBuyer(Long buyerId) {
        return getTransactionsByBuyer(buyerId).size();
    }

    @Transactional(readOnly = true)
    public long countTransactionsBySeller(Long sellerId) {
        return getTransactionsBySeller(sellerId).size();
    }
}

