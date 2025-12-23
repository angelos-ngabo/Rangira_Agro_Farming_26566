package com.raf.repository;

import com.raf.entity.Withdrawal;
import com.raf.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

Optional<Withdrawal> findByWithdrawalCode(String withdrawalCode);

List<Withdrawal> findByWalletId(Long walletId);

List<Withdrawal> findByStatus(WithdrawalStatus status);

boolean existsByWithdrawalCode(String withdrawalCode);

@Query("SELECT w FROM Withdrawal w WHERE w.wallet.user.id = :userId ORDER BY w.requestDate DESC")
List<Withdrawal> findByUserId(@Param("userId") Long userId);
}

