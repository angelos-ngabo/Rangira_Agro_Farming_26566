package com.raf.repository;

import com.raf.entity.UserProfile;
import com.raf.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByNationalId(String nationalId);
    List<UserProfile> findByGender(Gender gender);
    List<UserProfile> findByVerified(Boolean verified);
    List<UserProfile> findByAverageRatingGreaterThanEqual(BigDecimal rating);
    
    boolean existsByUserId(Long userId);
    boolean existsByNationalId(String nationalId);
    boolean existsByVerified(Boolean verified);
    
    @Query("SELECT up FROM UserProfile up WHERE up.averageRating >= :minRating AND up.verified = true")
    List<UserProfile> findVerifiedProfilesWithMinRating(@Param("minRating") BigDecimal minRating);
    
    Page<UserProfile> findByVerified(Boolean verified, Pageable pageable);
}

