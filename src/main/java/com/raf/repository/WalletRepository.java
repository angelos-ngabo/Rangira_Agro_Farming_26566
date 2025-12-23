package com.raf.repository;

import com.raf.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

Optional<Wallet> findByUserId(Long userId);

@Query("SELECT SUM(w.balance) FROM Wallet w")
java.math.BigDecimal getTotalSystemBalance();

@Query("SELECT SUM(w.totalEarned) FROM Wallet w")
java.math.BigDecimal getTotalSystemEarnings();
}

