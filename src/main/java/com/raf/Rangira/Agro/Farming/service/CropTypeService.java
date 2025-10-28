package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.CropType;
import com.raf.Rangira.Agro.Farming.enums.CropCategory;
import com.raf.Rangira.Agro.Farming.exception.DuplicateResourceException;
import com.raf.Rangira.Agro.Farming.exception.ResourceNotFoundException;
import com.raf.Rangira.Agro.Farming.repository.CropTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CropType Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CropTypeService {
    
    private final CropTypeRepository cropTypeRepository;
    
    /**
     * Create a new crop type
     */
    public CropType createCropType(CropType cropType) {
        // Check if crop code already exists
        if (cropTypeRepository.existsByCropCode(cropType.getCropCode())) {
            throw new DuplicateResourceException("Crop type with code " + cropType.getCropCode() + " already exists");
        }
        
        log.info("Creating new crop type: {}", cropType.getCropName());
        return cropTypeRepository.save(cropType);
    }
    
    /**
     * Get all crop types
     */
    @Transactional(readOnly = true)
    public List<CropType> getAllCropTypes() {
        return cropTypeRepository.findAll();
    }
    
    /**
     * Get crop types with pagination
     */
    @Transactional(readOnly = true)
    public Page<CropType> getCropTypesPaginated(Pageable pageable) {
        return cropTypeRepository.findAll(pageable);
    }
    
    /**
     * Get crop type by ID
     */
    @Transactional(readOnly = true)
    public CropType getCropTypeById(Long id) {
        return cropTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + id));
    }
    
    /**
     * Get crop type by code
     */
    @Transactional(readOnly = true)
    public CropType getCropTypeByCode(String cropCode) {
        return cropTypeRepository.findByCropCode(cropCode)
            .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with code: " + cropCode));
    }
    
    /**
     * Get crop types by category
     */
    @Transactional(readOnly = true)
    public List<CropType> getCropTypesByCategory(CropCategory category) {
        return cropTypeRepository.findByCategory(category);
    }
    
    /**
     * Search crop types by name
     */
    @Transactional(readOnly = true)
    public List<CropType> searchCropTypesByName(String name) {
        return cropTypeRepository.findByCropNameContainingIgnoreCase(name);
    }
    
    /**
     * Update crop type
     */
    public CropType updateCropType(Long id, CropType cropTypeDetails) {
        CropType cropType = getCropTypeById(id);
        
        cropType.setCropName(cropTypeDetails.getCropName());
        cropType.setCategory(cropTypeDetails.getCategory());
        cropType.setMeasurementUnit(cropTypeDetails.getMeasurementUnit());
        cropType.setDescription(cropTypeDetails.getDescription());
        
        log.info("Updating crop type ID: {}", id);
        return cropTypeRepository.save(cropType);
    }
    
    /**
     * Delete crop type
     */
    public void deleteCropType(Long id) {
        CropType cropType = getCropTypeById(id);
        log.info("Deleting crop type ID: {}", id);
        cropTypeRepository.delete(cropType);
    }
    
    /**
     * Check if crop code exists
     */
    @Transactional(readOnly = true)
    public boolean cropCodeExists(String cropCode) {
        return cropTypeRepository.existsByCropCode(cropCode);
    }
    
    /**
     * Get total number of crop types
     */
    @Transactional(readOnly = true)
    public long getTotalCropTypes() {
        return cropTypeRepository.count();
    }
}

