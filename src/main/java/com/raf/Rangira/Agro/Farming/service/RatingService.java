package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.Rating;
import com.raf.Rangira.Agro.Farming.enums.RatingType;
import com.raf.Rangira.Agro.Farming.exception.ResourceNotFoundException;
import com.raf.Rangira.Agro.Farming.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Rating Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RatingService {
    
    private final RatingRepository ratingRepository;
    
    /**
     * Create a new rating
     */
    public Rating createRating(Rating rating) {
        log.info("Creating new rating for user ID: {}", rating.getRatedUser().getId());
        return ratingRepository.save(rating);
    }
    
    /**
     * Get all ratings
     */
    @Transactional(readOnly = true)
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
    
    /**
     * Get ratings with pagination
     */
    @Transactional(readOnly = true)
    public Page<Rating> getRatingsPaginated(Pageable pageable) {
        return ratingRepository.findAll(pageable);
    }
    
    /**
     * Get rating by ID
     */
    @Transactional(readOnly = true)
    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + id));
    }
    
    /**
     * Get ratings for a user (ratings received)
     */
    @Transactional(readOnly = true)
    public List<Rating> getRatingsForUser(Long userId) {
        return ratingRepository.findByRatedUserId(userId);
    }
    
    /**
     * Get ratings by a user (ratings given)
     */
    @Transactional(readOnly = true)
    public List<Rating> getRatingsByUser(Long userId) {
        return ratingRepository.findByRaterId(userId);
    }
    
    /**
     * Get ratings by transaction
     */
    @Transactional(readOnly = true)
    public List<Rating> getRatingsByTransaction(Long transactionId) {
        return ratingRepository.findByTransactionId(transactionId);
    }
    
    /**
     * Get ratings by type
     */
    @Transactional(readOnly = true)
    public List<Rating> getRatingsByType(RatingType ratingType) {
        return ratingRepository.findByRatingType(ratingType);
    }
    
    /**
     * Get ratings for user by type
     */
    @Transactional(readOnly = true)
    public List<Rating> getRatingsForUserByType(Long userId, RatingType ratingType) {
        return ratingRepository.findByRatedUserIdAndRatingType(userId, ratingType);
    }
    
    /**
     * Update rating
     */
    public Rating updateRating(Long id, Rating ratingDetails) {
        Rating rating = getRatingById(id);
        
        rating.setRatingScore(ratingDetails.getRatingScore());
        rating.setComment(ratingDetails.getComment());
        
        log.info("Updating rating ID: {}", id);
        return ratingRepository.save(rating);
    }
    
    /**
     * Delete rating
     */
    public void deleteRating(Long id) {
        Rating rating = getRatingById(id);
        log.info("Deleting rating ID: {}", id);
        ratingRepository.delete(rating);
    }
    
    /**
     * Get total number of ratings
     */
    @Transactional(readOnly = true)
    public long getTotalRatings() {
        return ratingRepository.count();
    }
    
    /**
     * Get average rating for user
     */
    @Transactional(readOnly = true)
    public Double getAverageRatingForUser(Long userId) {
        List<Rating> ratings = ratingRepository.findByRatedUserId(userId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
            .mapToInt(Rating::getRatingScore)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Get average rating for user by type
     */
    @Transactional(readOnly = true)
    public Double getAverageRatingForUserByType(Long userId, RatingType ratingType) {
        List<Rating> ratings = ratingRepository.findByRatedUserIdAndRatingType(userId, ratingType);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
            .mapToInt(Rating::getRatingScore)
            .average()
            .orElse(0.0);
    }
}

