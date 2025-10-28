package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.entity.Rating;
import com.raf.Rangira.Agro.Farming.enums.RatingType;
import com.raf.Rangira.Agro.Farming.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rating REST Controller
 */
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating", description = "Rating management APIs")
public class RatingController {
    
    private final RatingService ratingService;
    
    @PostMapping
    @Operation(summary = "Create a new rating")
    public ResponseEntity<Rating> createRating(@Valid @RequestBody Rating rating) {
        Rating createdRating = ratingService.createRating(rating);
        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all ratings")
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get ratings with pagination")
    public ResponseEntity<Page<Rating>> getRatingsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("ratingDate").descending());
        Page<Rating> ratings = ratingService.getRatingsPaginated(pageRequest);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID")
    public ResponseEntity<Rating> getRatingById(@PathVariable Long id) {
        Rating rating = ratingService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get ratings for a user (ratings received)")
    public ResponseEntity<List<Rating>> getRatingsForUser(@PathVariable Long userId) {
        List<Rating> ratings = ratingService.getRatingsForUser(userId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get ratings by a user (ratings given)")
    public ResponseEntity<List<Rating>> getRatingsByUser(@PathVariable Long userId) {
        List<Rating> ratings = ratingService.getRatingsByUser(userId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get ratings by transaction")
    public ResponseEntity<List<Rating>> getRatingsByTransaction(@PathVariable Long transactionId) {
        List<Rating> ratings = ratingService.getRatingsByTransaction(transactionId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/type/{ratingType}")
    @Operation(summary = "Get ratings by type")
    public ResponseEntity<List<Rating>> getRatingsByType(@PathVariable RatingType ratingType) {
        List<Rating> ratings = ratingService.getRatingsByType(ratingType);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/user/{userId}/type/{ratingType}")
    @Operation(summary = "Get ratings for user by type")
    public ResponseEntity<List<Rating>> getRatingsForUserByType(
            @PathVariable Long userId,
            @PathVariable RatingType ratingType) {
        List<Rating> ratings = ratingService.getRatingsForUserByType(userId, ratingType);
        return ResponseEntity.ok(ratings);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update rating")
    public ResponseEntity<Rating> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody Rating rating) {
        Rating updatedRating = ratingService.updateRating(id, rating);
        return ResponseEntity.ok(updatedRating);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rating")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get total number of ratings")
    public ResponseEntity<Long> getTotalRatings() {
        long count = ratingService.getTotalRatings();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/user/{userId}/average")
    @Operation(summary = "Get average rating for user")
    public ResponseEntity<Double> getAverageRatingForUser(@PathVariable Long userId) {
        Double average = ratingService.getAverageRatingForUser(userId);
        return ResponseEntity.ok(average);
    }
    
    @GetMapping("/user/{userId}/type/{ratingType}/average")
    @Operation(summary = "Get average rating for user by type")
    public ResponseEntity<Double> getAverageRatingForUserByType(
            @PathVariable Long userId,
            @PathVariable RatingType ratingType) {
        Double average = ratingService.getAverageRatingForUserByType(userId, ratingType);
        return ResponseEntity.ok(average);
    }
}

