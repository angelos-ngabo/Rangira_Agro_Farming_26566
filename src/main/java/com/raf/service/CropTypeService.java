package com.raf.service;

import com.raf.entity.CropType;
import com.raf.enums.CropCategory;
import com.raf.exception.DuplicateResourceException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.CropTypeRepository;
import org.springframework.context.annotation.Lazy;
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
public class CropTypeService {
private final CropTypeRepository cropTypeRepository;
@Lazy
private final NotificationService notificationService;

public CropType createCropType(CropType cropType) {
if (cropTypeRepository.existsByCropCode(cropType.getCropCode())) {
throw new DuplicateResourceException("Crop type with code " + cropType.getCropCode() + " already exists");
}

log.info("Creating new crop type: {}", cropType.getCropName());
CropType saved = cropTypeRepository.save(cropType);


try {
notificationService.notifyAllAdmins(
"New Crop Type Created",
String.format("A new crop type '%s' (%s) has been created.",
saved.getCropName(),
saved.getCropCode()),
com.raf.enums.NotificationType.CROP_TYPE_CREATED,
"/crop-types"
);
} catch (Exception e) {
log.error("Failed to send notification for crop type creation: {}", e.getMessage());
}

return saved;
}

@Transactional(readOnly = true)
public List<CropType> getAllCropTypes() {
log.info("📊 Fetching all crop types from database...");
List<CropType> cropTypes = cropTypeRepository.findAll();
log.info("✅ Successfully fetched {} crop types from database", cropTypes.size());
if (cropTypes.isEmpty()) {
log.warn("⚠️  WARNING: No crop types found in database. Database may be empty or crop_types table is not populated.");
} else {
log.info("📋 Crop types found: {}", cropTypes.stream()
.map(ct -> String.format("%s (%s)", ct.getCropName(), ct.getCropCode()))
.collect(java.util.stream.Collectors.joining(", ")));
}
return cropTypes;
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
public List<CropType> getCropTypesByCategory(CropCategory category, Sort sort) {
return cropTypeRepository.findByCategory(category, sort);
}

@Transactional(readOnly = true)
public Page<CropType> getCropTypesByCategory(CropCategory category, Pageable pageable) {
return cropTypeRepository.findByCategory(category, pageable);
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
CropType saved = cropTypeRepository.save(cropType);


try {
notificationService.notifyAllAdmins(
"Crop Type Updated",
String.format("Crop type '%s' (%s) has been updated.",
saved.getCropName(),
saved.getCropCode()),
com.raf.enums.NotificationType.CROP_TYPE_UPDATED,
"/crop-types"
);
} catch (Exception e) {
log.error("Failed to send notification for crop type update: {}", e.getMessage());
}

return saved;
}

public void deleteCropType(Long id) {
CropType cropType = cropTypeRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Crop type not found with ID: " + id));
String cropName = cropType.getCropName();
String cropCode = cropType.getCropCode();
log.info("Deleting crop type ID: {}", id);
cropTypeRepository.delete(cropType);


try {
notificationService.notifyAllAdmins(
"Crop Type Deleted",
String.format("Crop type '%s' (%s) has been deleted.", cropName, cropCode),
com.raf.enums.NotificationType.CROP_TYPE_DELETED,
"/crop-types"
);
} catch (Exception e) {
log.error("Failed to send notification for crop type deletion: {}", e.getMessage());
}
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

