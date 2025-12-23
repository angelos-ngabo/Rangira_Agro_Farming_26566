package com.raf.controller;

import com.raf.dto.CropRatingRequest;
import com.raf.entity.Rating;
import com.raf.service.CropRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crop-ratings")
@RequiredArgsConstructor
@Tag(name = "Crop Ratings", description = "Crop rating management APIs (Admin only)")
public class CropRatingController {

private final CropRatingService cropRatingService;

@PostMapping
@Operation(summary = "Rate stored crop", description = "Admin only - Rate a stored crop (inventory) based on quality")
public ResponseEntity<Rating> rateStoredCrop(
@RequestHeader("Authorization") String authHeader,
@Valid @RequestBody CropRatingRequest request) {
String token = authHeader.substring(7);
Rating rating = cropRatingService.rateStoredCrop(token, request);
return new ResponseEntity<>(rating, HttpStatus.CREATED);
}

@GetMapping("/inventory/{inventoryId}")
@Operation(summary = "Get ratings for inventory", description = "Get all ratings for a specific stored crop (inventory)")
public ResponseEntity<List<Rating>> getRatingsForInventory(@PathVariable Long inventoryId) {
List<Rating> ratings = cropRatingService.getRatingsForInventory(inventoryId);
return ResponseEntity.ok(ratings);
}

@GetMapping("/admin/my-ratings")
@Operation(summary = "Get admin ratings", description = "Admin only - Get all ratings given by the logged-in admin")
public ResponseEntity<List<Rating>> getAdminRatings(
@RequestHeader("Authorization") String authHeader) {
String token = authHeader.substring(7);
List<Rating> ratings = cropRatingService.getAdminRatings(token);
return ResponseEntity.ok(ratings);
}
}

