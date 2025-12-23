package com.raf.repository;

import com.raf.entity.CropType;
import com.raf.enums.CropCategory;
import com.raf.enums.MeasurementUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropTypeRepository extends JpaRepository<CropType, Long> {

Optional<CropType> findByCropCode(String cropCode);
Optional<CropType> findByCropName(String cropName);
List<CropType> findByCropNameContainingIgnoreCase(String name);
List<CropType> findByCategory(CropCategory category);
List<CropType> findByCategory(CropCategory category, Sort sort);
List<CropType> findByMeasurementUnit(MeasurementUnit unit);
List<CropType> findByMeasurementUnit(MeasurementUnit unit, Sort sort);
List<CropType> findByCategoryAndMeasurementUnit(CropCategory category, MeasurementUnit unit);
List<CropType> findByCategoryAndMeasurementUnit(CropCategory category, MeasurementUnit unit, Sort sort);

boolean existsByCropCode(String cropCode);
boolean existsByCropName(String cropName);
boolean existsByCategory(CropCategory category);

@Query("SELECT ct FROM CropType ct WHERE ct.category = :category ORDER BY ct.cropName ASC")
List<CropType> findCropTypesByCategorySorted(@Param("category") CropCategory category);

@Query("SELECT COUNT(ct) FROM CropType ct WHERE ct.category = :category")
long countByCategory(@Param("category") CropCategory category);

Page<CropType> findByCategory(CropCategory category, Pageable pageable);
}

