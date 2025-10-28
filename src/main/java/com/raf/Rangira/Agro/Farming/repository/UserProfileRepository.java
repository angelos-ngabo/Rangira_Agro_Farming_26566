package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.UserProfile;
import com.raf.Rangira.Agro.Farming.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * UserProfile Repository
 * ONE-TO-ONE relationship with User
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    // findBy methods
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByNationalId(String nationalId);
    List<UserProfile> findByGender(Gender gender);
    List<UserProfile> findByVerified(Boolean verified);
    List<UserProfile> findByAverageRatingGreaterThanEqual(BigDecimal rating);
    
    // existsBy methods
    boolean existsByUserId(Long userId);
    boolean existsByNationalId(String nationalId);
    boolean existsByVerified(Boolean verified);
    
    // Custom queries
    @Query("SELECT up FROM UserProfile up WHERE up.averageRating >= :minRating AND up.verified = true")
    List<UserProfile> findVerifiedProfilesWithMinRating(@Param("minRating") BigDecimal minRating);
    
    // Pagination
    Page<UserProfile> findByVerified(Boolean verified, Pageable pageable);
}

