package com.raf.repository;

import com.raf.entity.Rating;
import com.raf.enums.RatingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

List<Rating> findByRaterId(Long raterId);
List<Rating> findByRaterId(Long raterId, Sort sort);
List<Rating> findByRatedUserId(Long ratedUserId);
List<Rating> findByRatedUserId(Long ratedUserId, Sort sort);
List<Rating> findByTransactionId(Long transactionId);
List<Rating> findByRatingType(RatingType ratingType);
List<Rating> findByRatingType(RatingType ratingType, Sort sort);
List<Rating> findByRatingScore(Integer ratingScore);
List<Rating> findByRatingScoreGreaterThanEqual(Integer score);

List<Rating> findByRatedUserIdAndRatingType(Long ratedUserId, RatingType ratingType);
List<Rating> findByRatedUserIdAndRatingScoreGreaterThanEqual(Long ratedUserId, Integer minScore);
List<Rating> findByRatingTypeAndRatingScore(RatingType ratingType, Integer score);

boolean existsByRaterIdAndTransactionId(Long raterId, Long transactionId);
boolean existsByRatedUserIdAndTransactionId(Long ratedUserId, Long transactionId);
boolean existsByTransactionId(Long transactionId);

@Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.ratedUser.id = :userId")
BigDecimal getAverageRatingForUser(@Param("userId") Long userId);

@Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.ratedUser.id = :userId AND r.ratingType = :ratingType")
BigDecimal getAverageRatingByType(@Param("userId") Long userId, @Param("ratingType") RatingType ratingType);

@Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUser.id = :userId AND r.ratingScore >= :minScore")
long countPositiveRatings(@Param("userId") Long userId, @Param("minScore") Integer minScore);

@Query("SELECT r FROM Rating r WHERE r.ratedUser.id = :userId ORDER BY r.createdAt DESC")
List<Rating> findRecentRatingsForUser(@Param("userId") Long userId, Pageable pageable);

@Query("SELECT r FROM Rating r WHERE r.ratedUser.id = :userId AND r.ratingType = :ratingType ORDER BY r.ratingScore DESC")
List<Rating> findRatingsByUserAndType(@Param("userId") Long userId, @Param("ratingType") RatingType ratingType);

@Query("SELECT DISTINCT r FROM Rating r " +
"LEFT JOIN FETCH r.rater " +
"LEFT JOIN FETCH r.ratedUser " +
"LEFT JOIN FETCH r.transaction t " +
"LEFT JOIN FETCH t.buyer " +
"LEFT JOIN FETCH t.seller " +
"LEFT JOIN FETCH t.inventory i " +
"LEFT JOIN FETCH i.cropType")
List<Rating> findAllWithRelations();

@Query("SELECT DISTINCT r FROM Rating r " +
"LEFT JOIN FETCH r.rater " +
"LEFT JOIN FETCH r.ratedUser " +
"LEFT JOIN FETCH r.transaction t " +
"LEFT JOIN FETCH t.buyer " +
"LEFT JOIN FETCH t.seller " +
"LEFT JOIN FETCH t.inventory i " +
"LEFT JOIN FETCH i.cropType")
Page<Rating> findAllWithRelations(Pageable pageable);

@Query("SELECT DISTINCT r FROM Rating r " +
"LEFT JOIN FETCH r.rater " +
"LEFT JOIN FETCH r.ratedUser " +
"LEFT JOIN FETCH r.transaction t " +
"LEFT JOIN FETCH t.buyer " +
"LEFT JOIN FETCH t.seller " +
"LEFT JOIN FETCH t.inventory i " +
"LEFT JOIN FETCH i.cropType " +
"WHERE r.rater.id = :raterId")
Page<Rating> findByRaterIdWithRelations(@Param("raterId") Long raterId, Pageable pageable);

@Query("SELECT DISTINCT r FROM Rating r " +
"LEFT JOIN FETCH r.rater " +
"LEFT JOIN FETCH r.ratedUser " +
"LEFT JOIN FETCH r.transaction t " +
"LEFT JOIN FETCH t.buyer " +
"LEFT JOIN FETCH t.seller " +
"LEFT JOIN FETCH t.inventory i " +
"LEFT JOIN FETCH i.cropType " +
"WHERE t.id = :transactionId")
List<Rating> findByTransactionIdWithRelations(@Param("transactionId") Long transactionId);

Page<Rating> findByRatedUserId(Long ratedUserId, Pageable pageable);
Page<Rating> findByRaterId(Long raterId, Pageable pageable);
}

