package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.Rangira.Agro.Farming.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * UserProfile Entity
 * ONE-TO-ONE relationship with User
 * Extended user information
 */
@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserProfile extends BaseEntity {
    
    // One-to-One relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @NotNull(message = "User is required")
    @JsonBackReference("user-profile")
    @ToString.Exclude
    private User user;
    
    @Column(name = "national_id", unique = true, length = 16)
    private String nationalId;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Column(name = "bio", columnDefinition = "text")
    private String bio;
    
    @Column(name = "verified")
    private Boolean verified = false;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
}

