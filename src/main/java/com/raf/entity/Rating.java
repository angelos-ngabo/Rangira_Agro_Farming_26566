package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.RatingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rating extends BaseEntity {

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "rater_id", nullable = false)
@NotNull(message = "Rater is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "notifications", "warehouseAccesses", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "wallet", "supportedCropTypes", "ratingsGiven", "ratingsReceived", "payments", "interestedCropTypes"})
@ToString.Exclude
private User rater;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "rated_user_id", nullable = false)
@NotNull(message = "Rated user is required")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "notifications", "warehouseAccesses", "inventoriesAsFarmer", "inventoriesAsStorekeeper", "transactionsAsBuyer", "transactionsAsSeller", "enquiriesAsBuyer", "enquiriesAsFarmer", "wallet", "supportedCropTypes", "ratingsGiven", "ratingsReceived", "payments", "interestedCropTypes"})
@ToString.Exclude
private User ratedUser;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "transaction_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "notifications", "enquiry", "buyer", "seller", "inventory", "payments", "ratings"})
@ToString.Exclude
private Transaction transaction;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "inventory_id")
@JsonBackReference("inventory-ratings")
@ToString.Exclude
private Inventory inventory;

@Column(name = "rating_score", nullable = false)
@NotNull(message = "Rating score is required")
@Min(value = 1, message = "Rating must be at least 1")
@Max(value = 5, message = "Rating must be at most 5")
private Integer ratingScore;

@Enumerated(EnumType.STRING)
@Column(name = "rating_type", nullable = false, length = 30)
@NotNull(message = "Rating type is required")
private RatingType ratingType;

@Column(name = "comment", columnDefinition = "text")
private String comment;
}

