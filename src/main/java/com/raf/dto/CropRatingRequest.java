package com.raf.dto;

import com.raf.enums.RatingType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CropRatingRequest {
@NotNull(message = "Inventory ID is required")
private Long inventoryId;

@NotNull(message = "Rating score is required")
@Min(value = 1, message = "Rating must be at least 1")
@Max(value = 5, message = "Rating must be at most 5")
private Integer ratingScore;

@NotNull(message = "Rating type is required")
private RatingType ratingType;

private String comment;
}

