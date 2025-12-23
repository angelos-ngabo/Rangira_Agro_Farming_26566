package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Wallet extends BaseEntity {

@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false, unique = true)
@NotNull(message = "User is required")
@JsonBackReference("user-wallet")
@ToString.Exclude
private User user;

@Column(name = "balance", precision = 12, scale = 2, nullable = false)
private BigDecimal balance = BigDecimal.ZERO;

@Column(name = "total_earned", precision = 12, scale = 2, nullable = false)
private BigDecimal totalEarned = BigDecimal.ZERO;

@Column(name = "total_withdrawn", precision = 12, scale = 2, nullable = false)
private BigDecimal totalWithdrawn = BigDecimal.ZERO;

@OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private List<Withdrawal> withdrawals = new ArrayList<>();
}

