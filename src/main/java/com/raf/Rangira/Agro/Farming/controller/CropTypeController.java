package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.entity.CropType;
import com.raf.Rangira.Agro.Farming.enums.CropCategory;
import com.raf.Rangira.Agro.Farming.service.CropTypeService;
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
 * CropType REST Controller
 */
@RestController
@RequestMapping("/api/crop-types")
@RequiredArgsConstructor
@Tag(name = "CropType", description = "Crop type management APIs")
public class CropTypeController {
    
    private final CropTypeService cropTypeService;
    
    @PostMapping
    @Operation(summary = "Create a new crop type")
    public ResponseEntity<CropType> createCropType(@Valid @RequestBody CropType cropType) {
        CropType createdCropType = cropTypeService.createCropType(cropType);
        return new ResponseEntity<>(createdCropType, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all crop types")
    public ResponseEntity<List<CropType>> getAllCropTypes() {
        List<CropType> cropTypes = cropTypeService.getAllCropTypes();
        return ResponseEntity.ok(cropTypes);
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get crop types with pagination")
    public ResponseEntity<Page<CropType>> getCropTypesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("cropName"));
        Page<CropType> cropTypes = cropTypeService.getCropTypesPaginated(pageRequest);
        return ResponseEntity.ok(cropTypes);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get crop type by ID")
    public ResponseEntity<CropType> getCropTypeById(@PathVariable Long id) {
        CropType cropType = cropTypeService.getCropTypeById(id);
        return ResponseEntity.ok(cropType);
    }
    
    @GetMapping("/code/{cropCode}")
    @Operation(summary = "Get crop type by code")
    public ResponseEntity<CropType> getCropTypeByCode(@PathVariable String cropCode) {
        CropType cropType = cropTypeService.getCropTypeByCode(cropCode);
        return ResponseEntity.ok(cropType);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get crop types by category")
    public ResponseEntity<List<CropType>> getCropTypesByCategory(@PathVariable CropCategory category) {
        List<CropType> cropTypes = cropTypeService.getCropTypesByCategory(category);
        return ResponseEntity.ok(cropTypes);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search crop types by name")
    public ResponseEntity<List<CropType>> searchCropTypesByName(@RequestParam String name) {
        List<CropType> cropTypes = cropTypeService.searchCropTypesByName(name);
        return ResponseEntity.ok(cropTypes);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update crop type")
    public ResponseEntity<CropType> updateCropType(
            @PathVariable Long id,
            @Valid @RequestBody CropType cropType) {
        CropType updatedCropType = cropTypeService.updateCropType(id, cropType);
        return ResponseEntity.ok(updatedCropType);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete crop type")
    public ResponseEntity<Void> deleteCropType(@PathVariable Long id) {
        cropTypeService.deleteCropType(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/exists/code/{cropCode}")
    @Operation(summary = "Check if crop code exists")
    public ResponseEntity<Boolean> cropCodeExists(@PathVariable String cropCode) {
        boolean exists = cropTypeService.cropCodeExists(cropCode);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get total number of crop types")
    public ResponseEntity<Long> getTotalCropTypes() {
        long count = cropTypeService.getTotalCropTypes();
        return ResponseEntity.ok(count);
    }
}

