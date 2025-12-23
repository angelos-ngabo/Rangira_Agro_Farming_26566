package com.raf.service;

import com.raf.dto.WithdrawalRequest;
import com.raf.entity.User;
import com.raf.entity.Wallet;
import com.raf.entity.Withdrawal;
import com.raf.enums.WithdrawalStatus;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WalletRepository;
import com.raf.repository.WithdrawalRepository;
import com.raf.service.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletService {

private final WalletRepository walletRepository;
private final WithdrawalRepository withdrawalRepository;
private final UserRepository userRepository;
private final TransactionRepository transactionRepository;
private final EmailService emailService;

@PersistenceContext
private EntityManager entityManager;

@Transactional(readOnly = true)
public Wallet getWalletByUserId(Long userId) {
return walletRepository.findByUserId(userId)
.orElseGet(() -> {

User user = userRepository.findById(userId)
.orElseThrow(() -> new ResourceNotFoundException("User not found"));
Wallet wallet = new Wallet();
wallet.setUser(user);
wallet.setBalance(BigDecimal.ZERO);
wallet.setTotalEarned(BigDecimal.ZERO);
wallet.setTotalWithdrawn(BigDecimal.ZERO);
return walletRepository.save(wallet);
});
}

public Withdrawal requestWithdrawal(Long userId, WithdrawalRequest request) {
log.info("Processing withdrawal request for user {}", userId);

Wallet wallet = getWalletByUserId(userId);
User user = userRepository.findById(userId)
.orElseThrow(() -> new ResourceNotFoundException("User not found"));

if (request.getAmount().compareTo(wallet.getBalance()) > 0) {
throw new IllegalArgumentException("Insufficient balance");
}

if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
}


final String withdrawalCode;
{
String tempCode;
do {
tempCode = "WTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
} while (withdrawalRepository.existsByWithdrawalCode(tempCode));
withdrawalCode = tempCode;
}

String otpCode = String.format("%06d", (int)(Math.random() * 1000000));
LocalDateTime otpExpiresAt = LocalDateTime.now().plusMinutes(10);

Withdrawal withdrawal = new Withdrawal();
withdrawal.setWithdrawalCode(withdrawalCode);
withdrawal.setWallet(wallet);
withdrawal.setAmount(request.getAmount());
withdrawal.setWithdrawalMethod(request.getWithdrawalMethod());
withdrawal.setAccountNumber(request.getAccountNumber());
withdrawal.setAccountName(request.getAccountName());
withdrawal.setBankName(request.getBankName());
withdrawal.setStatus(WithdrawalStatus.PENDING);
withdrawal.setRequestDate(LocalDateTime.now());
withdrawal.setOtpCode(otpCode);
withdrawal.setOtpExpiresAt(otpExpiresAt);
withdrawal.setOtpVerified(false);

Withdrawal saved = withdrawalRepository.save(withdrawal);
entityManager.flush();

log.info("Withdrawal request created with OTP: {}", withdrawalCode);

final String finalOtpCode = otpCode;
final String finalAmount = request.getAmount().toPlainString();
new java.lang.Thread(() -> {
try {
emailService.sendWithdrawalOtpEmail(
user.getEmail(),
finalOtpCode,
user.getFirstName(),
finalAmount
);
log.info("Withdrawal OTP sent to {} for withdrawal {}", user.getEmail(), withdrawalCode);
} catch (Exception e) {
log.error("Failed to send withdrawal OTP email: {}", e.getMessage());
}
}).start();

return saved;
}


public Withdrawal verifyOtpAndCompleteWithdrawal(Long withdrawalId, String otpCode) {
log.info("Verifying OTP for withdrawal {}", withdrawalId);

Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
.orElseThrow(() -> new ResourceNotFoundException("Withdrawal not found"));

if (withdrawal.getOtpVerified() != null && withdrawal.getOtpVerified()) {
throw new IllegalArgumentException("OTP has already been verified for this withdrawal");
}

if (withdrawal.getOtpExpiresAt() == null || withdrawal.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
throw new IllegalArgumentException("OTP has expired. Please request a new withdrawal.");
}

if (withdrawal.getOtpCode() == null || !withdrawal.getOtpCode().equals(otpCode)) {
throw new IllegalArgumentException("Invalid OTP code");
}


withdrawal.setOtpVerified(true);
withdrawal.setStatus(WithdrawalStatus.COMPLETED);
withdrawal.setProcessedDate(LocalDateTime.now());


Wallet wallet = withdrawal.getWallet();
wallet.setBalance(wallet.getBalance().subtract(withdrawal.getAmount()));
wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(withdrawal.getAmount()));

withdrawalRepository.save(withdrawal);
walletRepository.save(wallet);
entityManager.flush();

log.info("Withdrawal {} completed after OTP verification", withdrawal.getWithdrawalCode());
return withdrawal;
}

public Withdrawal processWithdrawal(Long withdrawalId, boolean approve, String notes) {
Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
.orElseThrow(() -> new ResourceNotFoundException("Withdrawal not found"));

if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
throw new IllegalArgumentException("Withdrawal is not pending");
}

if (approve) {
withdrawal.setStatus(WithdrawalStatus.COMPLETED);
withdrawal.setProcessedDate(LocalDateTime.now());
withdrawal.setProcessingNotes(notes);

Wallet wallet = withdrawal.getWallet();
wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(withdrawal.getAmount()));
walletRepository.save(wallet);
} else {
withdrawal.setStatus(WithdrawalStatus.FAILED);
withdrawal.setProcessedDate(LocalDateTime.now());
withdrawal.setProcessingNotes(notes);


Wallet wallet = withdrawal.getWallet();
wallet.setBalance(wallet.getBalance().add(withdrawal.getAmount()));
walletRepository.save(wallet);
}

withdrawalRepository.save(withdrawal);
entityManager.flush();
return withdrawal;
}

@Transactional(readOnly = true)
public List<Withdrawal> getWithdrawalsByUserId(Long userId) {
return withdrawalRepository.findByUserId(userId);
}

@Transactional(readOnly = true)
public BigDecimal getTotalSystemCommission() {
BigDecimal total = transactionRepository.getTotalSystemCommission();
return total != null ? total : BigDecimal.ZERO;
}
}

