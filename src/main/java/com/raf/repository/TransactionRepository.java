package com.raf.repository;

import com.raf.entity.Transaction;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionCode(String transactionCode);
    List<Transaction> findByBuyerId(Long buyerId);
    List<Transaction> findBySellerId(Long sellerId);
    List<Transaction> findByInventoryId(Long inventoryId);
    List<Transaction> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Transaction> findByDeliveryStatus(DeliveryStatus deliveryStatus);
    
    List<Transaction> findByBuyerIdAndPaymentStatus(Long buyerId, PaymentStatus paymentStatus);
    List<Transaction> findBySellerIdAndPaymentStatus(Long sellerId, PaymentStatus paymentStatus);
    List<Transaction> findByPaymentStatusAndDeliveryStatus(PaymentStatus paymentStatus, DeliveryStatus deliveryStatus);
    
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByTransactionDateAfter(LocalDateTime date);
    List<Transaction> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByTotalAmountGreaterThan(BigDecimal amount);
    List<Transaction> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    boolean existsByTransactionCode(String transactionCode);
    boolean existsByBuyerIdAndPaymentStatus(Long buyerId, PaymentStatus paymentStatus);
    boolean existsBySellerIdAndDeliveryStatus(Long sellerId, DeliveryStatus deliveryStatus);
    
    @Query("SELECT t FROM Transaction t WHERE t.buyer.id = :userId OR t.seller.id = :userId")
    List<Transaction> findTransactionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.buyer.id = :buyerId AND t.paymentStatus = :paymentStatus ORDER BY t.transactionDate DESC")
    List<Transaction> findBuyerTransactionsByPaymentStatus(@Param("buyerId") Long buyerId, 
                                                            @Param("paymentStatus") PaymentStatus paymentStatus);
    
    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :sellerId AND t.deliveryStatus = :deliveryStatus ORDER BY t.transactionDate DESC")
    List<Transaction> findSellerTransactionsByDeliveryStatus(@Param("sellerId") Long sellerId, 
                                                               @Param("deliveryStatus") DeliveryStatus deliveryStatus);
    
    @Query("SELECT SUM(t.netAmount) FROM Transaction t WHERE t.seller.id = :sellerId AND t.paymentStatus = 'PAID'")
    BigDecimal getTotalEarningsBySeller(@Param("sellerId") Long sellerId);
    
    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.buyer.id = :buyerId AND t.paymentStatus = 'PAID'")
    BigDecimal getTotalSpendingByBuyer(@Param("buyerId") Long buyerId);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.paymentStatus = :paymentStatus AND t.deliveryStatus = :deliveryStatus")
    long countByPaymentStatusAndDeliveryStatus(@Param("paymentStatus") PaymentStatus paymentStatus, 
                                                @Param("deliveryStatus") DeliveryStatus deliveryStatus);
    
    Page<Transaction> findByBuyerId(Long buyerId, Pageable pageable);
    Page<Transaction> findBySellerId(Long sellerId, Pageable pageable);
    Page<Transaction> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
}

