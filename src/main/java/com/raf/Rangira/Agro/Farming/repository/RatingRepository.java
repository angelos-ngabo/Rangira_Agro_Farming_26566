package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Rating;
import com.raf.Rangira.Agro.Farming.enums.RatingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Rating Repository
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    // findBy methods
    List<Rating> findByRaterId(Long raterId);
    List<Rating> findByRatedUserId(Long ratedUserId);
    List<Rating> findByTransactionId(Long transactionId);
    List<Rating> findByRatingType(RatingType ratingType);
    List<Rating> findByRatingScore(Integer ratingScore);
    List<Rating> findByRatingScoreGreaterThanEqual(Integer score);
    
    // Combined filters
    List<Rating> findByRatedUserIdAndRatingType(Long ratedUserId, RatingType ratingType);
    List<Rating> findByRatedUserIdAndRatingScoreGreaterThanEqual(Long ratedUserId, Integer minScore);
    List<Rating> findByRatingTypeAndRatingScore(RatingType ratingType, Integer score);
    
    // existsBy methods
    boolean existsByRaterIdAndTransactionId(Long raterId, Long transactionId);
    boolean existsByRatedUserIdAndTransactionId(Long ratedUserId, Long transactionId);
    boolean existsByTransactionId(Long transactionId);
    
    // Custom queries
    @Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.ratedUser.id = :userId")
    BigDecimal getAverageRatingForUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.ratedUser.id = :userId AND r.ratingType = :ratingType")
    BigDecimal getAverageRatingByType(@Param("userId") Long userId, @Param("ratingType") RatingType ratingType);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUser.id = :userId AND r.ratingScore >= :minScore")
    long countPositiveRatings(@Param("userId") Long userId, @Param("minScore") Integer minScore);
    
    @Query("SELECT r FROM Rating r WHERE r.ratedUser.id = :userId ORDER BY r.createdAt DESC")
    List<Rating> findRecentRatingsForUser(@Param("userId") Long userId, Pageable pageable);
    
    // Get ratings for a user by type
    @Query("SELECT r FROM Rating r WHERE r.ratedUser.id = :userId AND r.ratingType = :ratingType ORDER BY r.ratingScore DESC")
    List<Rating> findRatingsByUserAndType(@Param("userId") Long userId, @Param("ratingType") RatingType ratingType);
    
    // Pagination
    Page<Rating> findByRatedUserId(Long ratedUserId, Pageable pageable);
    Page<Rating> findByRaterId(Long raterId, Pageable pageable);
}

