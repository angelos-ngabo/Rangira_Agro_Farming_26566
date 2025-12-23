package com.raf.service;

import com.raf.dto.RatingRequest;
import com.raf.entity.Inventory;
import com.raf.entity.Rating;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.enums.RatingType;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.InventoryRepository;
import com.raf.repository.RatingRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RatingService {
private static final String RATING_NOT_FOUND_WITH_ID = "Rating not found with ID: ";

private final RatingRepository ratingRepository;
private final UserRepository userRepository;
private final TransactionRepository transactionRepository;
private final InventoryRepository inventoryRepository;

public Rating createRatingFromRequest(RatingRequest request) {
log.info("Creating rating from request for rated user ID: {}", request.getRatedUserId());

User rater = userRepository.findById(request.getRaterId())
.orElseThrow(() -> new ResourceNotFoundException("Rater not found with ID: " + request.getRaterId()));

User ratedUser = userRepository.findById(request.getRatedUserId())
.orElseThrow(() -> new ResourceNotFoundException("Rated user not found with ID: " + request.getRatedUserId()));

Rating rating = new Rating();
rating.setRater(rater);
rating.setRatedUser(ratedUser);
rating.setRatingScore(request.getRatingScore());
rating.setRatingType(request.getRatingType());
rating.setComment(request.getComment());


if (request.getTransactionId() != null) {
Transaction transaction = transactionRepository.findById(request.getTransactionId())
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + request.getTransactionId()));
rating.setTransaction(transaction);
}


if (request.getInventoryId() != null) {
Inventory inventory = inventoryRepository.findById(request.getInventoryId())
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + request.getInventoryId()));
rating.setInventory(inventory);
}

return ratingRepository.save(rating);
}

@Transactional(readOnly = true)
public List<Rating> getAllRatings() {
return ratingRepository.findAll();
}

@Transactional(readOnly = true)
public List<Rating> getAllRatings(String raterType, Long raterId) {
if (raterId != null) {
return ratingRepository.findByRaterId(raterId);
}
if (raterType != null) {

return ratingRepository.findAll().stream()
.filter(r -> r.getRater().getUserType().name().equals(raterType))
.toList();
}
return ratingRepository.findAll();
}

@Transactional(readOnly = true)
public Page<Rating> getRatingsPaginated(Pageable pageable) {
return ratingRepository.findAllWithRelations(pageable);
}

@Transactional(readOnly = true)
public Page<Rating> getRatingsPaginated(Pageable pageable, String raterType, Long raterId) {
if (raterId != null) {
return ratingRepository.findByRaterIdWithRelations(raterId, pageable);
}
if (raterType != null) {


List<Rating> allRatings = ratingRepository.findAllWithRelations();
List<Rating> filtered = allRatings.stream()
.filter(r -> r.getRater() != null && r.getRater().getUserType().name().equals(raterType))
.toList();

int start = (int) pageable.getOffset();
int end = Math.min(start + pageable.getPageSize(), filtered.size());
List<Rating> pageContent = filtered.subList(start, end);
return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
}
return ratingRepository.findAllWithRelations(pageable);
}

@Transactional(readOnly = true)
public Rating getRatingById(Long id) {
return ratingRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(RATING_NOT_FOUND_WITH_ID + id));
}

@Transactional(readOnly = true)
public List<Rating> getRatingsForUser(Long userId) {
return ratingRepository.findByRatedUserId(userId);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsForUser(Long userId, Sort sort) {
return ratingRepository.findByRatedUserId(userId, sort);
}

@Transactional(readOnly = true)
public Page<Rating> getRatingsForUser(Long userId, Pageable pageable) {
return ratingRepository.findByRatedUserId(userId, pageable);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsByUser(Long userId) {
return ratingRepository.findByRaterId(userId);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsByUser(Long userId, Sort sort) {
return ratingRepository.findByRaterId(userId, sort);
}

@Transactional(readOnly = true)
public Page<Rating> getRatingsByUser(Long userId, Pageable pageable) {
return ratingRepository.findByRaterId(userId, pageable);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsByType(RatingType ratingType, Sort sort) {
return ratingRepository.findByRatingType(ratingType, sort);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsByTransaction(Long transactionId) {
return ratingRepository.findByTransactionIdWithRelations(transactionId);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsByType(RatingType ratingType) {
return ratingRepository.findByRatingType(ratingType);
}

@Transactional(readOnly = true)
public List<Rating> getRatingsForUserByType(Long userId, RatingType ratingType) {
return ratingRepository.findByRatedUserIdAndRatingType(userId, ratingType);
}

public Rating updateRating(Long id, Rating ratingDetails) {
Rating rating = ratingRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(RATING_NOT_FOUND_WITH_ID + id));

rating.setRatingScore(ratingDetails.getRatingScore());
rating.setComment(ratingDetails.getComment());

log.info("Updating rating ID: {}", id);
return ratingRepository.save(rating);
}

public void deleteRating(Long id) {
Rating rating = ratingRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(RATING_NOT_FOUND_WITH_ID + id));
log.info("Deleting rating ID: {}", id);
ratingRepository.delete(rating);
}

@Transactional(readOnly = true)
public long getTotalRatings() {
return ratingRepository.count();
}

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

