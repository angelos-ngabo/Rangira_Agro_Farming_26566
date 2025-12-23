package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Message extends BaseEntity {

@NotBlank(message = "Message code is required")
@Column(name = "message_code", unique = true, nullable = false, length = 20)
private String messageCode;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "sender_id", nullable = false)
@NotNull(message = "Sender is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "notifications", "warehouseAccesses", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "wallet", "supportedCropTypes", "ratingsGiven", "ratingsReceived", "payments", "interestedCropTypes", "messagesSent", "messagesReceived"})
@ToString.Exclude
private User sender;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "receiver_id", nullable = false)
@NotNull(message = "Receiver is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "notifications", "warehouseAccesses", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "wallet", "supportedCropTypes", "ratingsGiven", "ratingsReceived", "payments", "interestedCropTypes", "messagesSent", "messagesReceived"})
@ToString.Exclude
private User receiver;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "related_inventory_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "messages", "enquiries", "ratings"})
@ToString.Exclude
private Inventory relatedInventory;

@NotBlank(message = "Subject is required")
@Column(name = "subject", nullable = false, length = 200)
private String subject;

@NotBlank(message = "Message content is required")
@Column(name = "content", columnDefinition = "text", nullable = false)
private String content;

@Column(name = "is_read", nullable = false)
private Boolean isRead = false;

@Column(name = "read_at")
private java.time.LocalDateTime readAt;

@Column(name = "replied_to_message_id")
private Long repliedToMessageId;
}

