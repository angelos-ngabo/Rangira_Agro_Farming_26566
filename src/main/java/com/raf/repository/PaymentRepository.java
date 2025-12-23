package com.raf.repository;

import com.raf.entity.Payment;
import com.raf.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

Optional<Payment> findByPaymentCode(String paymentCode);

List<Payment> findByTransactionId(Long transactionId);

List<Payment> findByPayerId(Long payerId);

List<Payment> findByStatus(PaymentStatus status);

boolean existsByPaymentCode(String paymentCode);

@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
java.math.BigDecimal getTotalAmountByStatus(@Param("status") PaymentStatus status);
}

