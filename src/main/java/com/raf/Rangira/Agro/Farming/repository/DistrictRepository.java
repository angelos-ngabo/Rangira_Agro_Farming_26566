package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * District Repository
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    
    // findBy methods
    Optional<District> findByDistrictCode(String districtCode);
    Optional<District> findByDistrictName(String districtName);
    List<District> findByDistrictNameContainingIgnoreCase(String name);
    
    // Find districts by province
    List<District> findByProvinceId(Long provinceId);
    List<District> findByProvinceProvinceCode(String provinceCode);
    List<District> findByProvinceProvinceName(String provinceName);
    
    // existsBy methods
    boolean existsByDistrictCode(String districtCode);
    boolean existsByDistrictName(String districtName);
    boolean existsByProvinceId(Long provinceId);
    
    // Custom query
    @Query("SELECT d FROM District d WHERE d.province.id = :provinceId ORDER BY d.districtName ASC")
    List<District> findDistrictsByProvinceIdSorted(@Param("provinceId") Long provinceId);
    
    // Pagination
    Page<District> findByProvinceId(Long provinceId, Pageable pageable);
}

