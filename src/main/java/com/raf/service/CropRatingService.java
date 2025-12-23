package com.raf.service;

import com.raf.dto.CropRatingRequest;
import com.raf.entity.Inventory;
import com.raf.entity.Rating;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.enums.UserType;
import com.raf.exception.ResourceNotFoundException;
import com.raf.exception.UnauthorizedException;
import com.raf.repository.InventoryRepository;
import com.raf.repository.RatingRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import com.raf.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CropRatingService {

private final RatingRepository ratingRepository;
private final InventoryRepository inventoryRepository;
private final TransactionRepository transactionRepository;
private final UserRepository userRepository;
private final JwtUtil jwtUtil;


public Rating rateStoredCrop(String token, CropRatingRequest request) {
Long adminId = jwtUtil.getUserIdFromToken(token);
User admin = userRepository.findById(adminId)
.orElseThrow(() -> new ResourceNotFoundException("User not found"));

if (admin.getUserType() != UserType.ADMIN) {
throw new UnauthorizedException("Only admins can rate stored crops");
}

Inventory inventory = inventoryRepository.findById(request.getInventoryId())
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + request.getInventoryId()));



List<Transaction> transactions = transactionRepository.findByInventoryId(inventory.getId());
if (transactions.isEmpty()) {
throw new UnauthorizedException("Cannot rate inventory that hasn't been part of any transaction. " +
"Crops must be sold before they can be rated.");
}


Transaction transaction = transactions.stream()
.max((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()))
.orElse(transactions.get(0));

User ratedUser = inventory.getFarmer();


Rating rating = new Rating();
rating.setRater(admin);
rating.setRatedUser(ratedUser);
rating.setTransaction(transaction);
rating.setRatingScore(request.getRatingScore());
rating.setRatingType(request.getRatingType());
rating.setComment(request.getComment() != null ?
request.getComment() + " (Rated crop: " + inventory.getCropType().getCropName() + ")" :
"Rated crop: " + inventory.getCropType().getCropName());

Rating savedRating = ratingRepository.save(rating);
log.info("Admin {} rated stored crop (inventory {}) with score {}",
admin.getEmail(), inventory.getInventoryCode(), request.getRatingScore());

return savedRating;
}


@Transactional(readOnly = true)
public List<Rating> getRatingsForInventory(Long inventoryId) {
Inventory inventory = inventoryRepository.findById(inventoryId)
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + inventoryId));


return ratingRepository.findByRatedUserId(inventory.getFarmer().getId());
}


@Transactional(readOnly = true)
public List<Rating> getAdminRatings(String token) {
Long adminId = jwtUtil.getUserIdFromToken(token);
return ratingRepository.findByRaterId(adminId);
}
}

