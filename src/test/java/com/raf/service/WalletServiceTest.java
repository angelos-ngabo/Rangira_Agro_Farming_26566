package com.raf.service;

import com.raf.dto.WithdrawalRequest;
import com.raf.entity.User;
import com.raf.entity.Wallet;
import com.raf.entity.Withdrawal;
import com.raf.enums.WithdrawalStatus;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WalletRepository;
import com.raf.repository.WithdrawalRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WithdrawalRepository withdrawalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WalletService walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(walletService, "entityManager", entityManager);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("50000.00"));
        wallet.setTotalWithdrawn(BigDecimal.ZERO);
    }

    @Test
    void shouldRejectWithdrawalWhenBalanceIsInsufficient() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(new BigDecimal("60000.00")); // Exceeds 50000

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.requestWithdrawal(1L, request);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    void shouldSuccessfullyRequestWithdrawal() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(withdrawalRepository.existsByWithdrawalCode(any(String.class))).thenReturn(false);
        when(withdrawalRepository.save(any(Withdrawal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(new BigDecimal("10000.00"));

        Withdrawal withdrawal = walletService.requestWithdrawal(1L, request);

        assertNotNull(withdrawal);
        assertEquals(WithdrawalStatus.PENDING, withdrawal.getStatus());
        assertEquals(new BigDecimal("10000.00"), withdrawal.getAmount());
        assertNotNull(withdrawal.getOtpCode());
        assertFalse(withdrawal.getOtpVerified());
    }

    @Test
    void shouldCompleteWithdrawalAfterOtpVerification() {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(1L);
        withdrawal.setWallet(wallet);
        withdrawal.setAmount(new BigDecimal("10000.00"));
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setOtpCode("123456");
        withdrawal.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));
        withdrawal.setOtpVerified(false);

        when(withdrawalRepository.findById(1L)).thenReturn(Optional.of(withdrawal));

        Withdrawal completed = walletService.verifyOtpAndCompleteWithdrawal(1L, "123456");

        assertTrue(completed.getOtpVerified());
        assertEquals(WithdrawalStatus.COMPLETED, completed.getStatus());
        
        // Wallet balance should decrease by 10000
        assertEquals(new BigDecimal("40000.00"), wallet.getBalance());
        assertEquals(new BigDecimal("10000.00"), wallet.getTotalWithdrawn());
    }
}
