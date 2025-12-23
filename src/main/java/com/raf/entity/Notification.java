package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification extends BaseEntity {

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
@NotNull(message = "User is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "notifications", "warehouseAccesses", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "wallet", "supportedCropTypes", "ratingsGiven", "ratingsReceived", "payments", "interestedCropTypes"})
@ToString.Exclude
private User user;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "transaction_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "notifications", "enquiry", "buyer", "seller", "inventory"})
@ToString.Exclude
private Transaction transaction;

@Enumerated(EnumType.STRING)
@Column(name = "type", nullable = false, length = 50)
@NotNull(message = "Notification type is required")
private NotificationType type;

@NotBlank(message = "Title is required")
@Column(name = "title", nullable = false, length = 200)
private String title;

@NotBlank(message = "Message is required")
@Column(name = "message", columnDefinition = "text", nullable = false)
private String message;

@Column(name = "is_read", nullable = false)
private Boolean isRead = false;

@Column(name = "read_at")
private LocalDateTime readAt;

@Column(name = "action_url", length = 500)
private String actionUrl;
}

