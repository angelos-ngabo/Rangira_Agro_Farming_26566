package com.raf.service;

import com.raf.entity.CropType;
import com.raf.enums.CropCategory;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.CropTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CropTypeService {
    private final CropTypeRepository cropTypeRepository;

    public CropType createCropType(CropType cropType) {
        if (cropTypeRepository.existsByCropCode(cropType.getCropCode())) {
            throw new DuplicateResourceException("Crop type with code " + cropType.getCropCode() + " already exists");
        }
        
        log.info("Creating new crop type: {}", cropType.getCropName());
        return cropTypeRepository.save(cropType);
    }

    @Transactional(readOnly = true)
    public List<CropType> getAllCropTypes() {
        return cropTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<CropType> getCropTypesPaginated(Pageable pageable) {
        return cropTypeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public CropType getCropTypeById(Long id) {
        return cropTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public CropType getCropTypeByCode(String cropCode) {
        return cropTypeRepository.findByCropCode(cropCode)
            .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with code: " + cropCode));
    }

    @Transactional(readOnly = true)
    public List<CropType> getCropTypesByCategory(CropCategory category) {
        return cropTypeRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<CropType> searchCropTypesByName(String name) {
        return cropTypeRepository.findByCropNameContainingIgnoreCase(name);
    }

    public CropType updateCropType(Long id, CropType cropTypeDetails) {
        CropType cropType = cropTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + id));
        
        cropType.setCropName(cropTypeDetails.getCropName());
        cropType.setCategory(cropTypeDetails.getCategory());
        cropType.setMeasurementUnit(cropTypeDetails.getMeasurementUnit());
        cropType.setDescription(cropTypeDetails.getDescription());
        
        log.info("Updating crop type ID: {}", id);
        return cropTypeRepository.save(cropType);
    }

    public void deleteCropType(Long id) {
        CropType cropType = cropTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + id));
        log.info("Deleting crop type ID: {}", id);
        cropTypeRepository.delete(cropType);
    }

    @Transactional(readOnly = true)
    public boolean cropCodeExists(String cropCode) {
        return cropTypeRepository.existsByCropCode(cropCode);
    }

    @Transactional(readOnly = true)
    public long getTotalCropTypes() {
        return cropTypeRepository.count();
    }
}

