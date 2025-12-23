package com.raf.dto;

import com.raf.enums.WithdrawalMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
@NotNull(message = "Amount is required")
private BigDecimal amount;

@NotNull(message = "Withdrawal method is required")
private WithdrawalMethod withdrawalMethod;

@NotBlank(message = "Account number is required")
private String accountNumber;

private String accountName;

private String bankName;

private String otpCode;
}

