package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.Rangira.Agro.Farming.enums.RatingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Rating Entity
 * Trust and rating system for users
 */
@Entity
@Table(name = "rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rating extends BaseEntity {
    
    // Many-to-One: User giving the rating
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    @NotNull(message = "Rater is required")
    @JsonBackReference
    @ToString.Exclude
    private User rater;
    
    // Many-to-One: User receiving the rating
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    @NotNull(message = "Rated user is required")
    @JsonBackReference
    @ToString.Exclude
    private User ratedUser;
    
    // Many-to-One: Transaction context
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    @NotNull(message = "Transaction is required")
    @JsonBackReference
    @ToString.Exclude
    private Transaction transaction;
    
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

