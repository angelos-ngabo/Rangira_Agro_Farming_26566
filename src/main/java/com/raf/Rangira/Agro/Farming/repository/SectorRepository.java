package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Sector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Sector Repository
 */
@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    
    // findBy methods
    Optional<Sector> findBySectorCode(String sectorCode);
    Optional<Sector> findBySectorName(String sectorName);
    List<Sector> findBySectorNameContainingIgnoreCase(String name);
    
    // Find by district
    List<Sector> findByDistrictId(Long districtId);
    List<Sector> findByDistrictDistrictCode(String districtCode);
    
    // Find by province (nested relationship)
    List<Sector> findByDistrictProvinceId(Long provinceId);
    List<Sector> findByDistrictProvinceProvinceCode(String provinceCode);
    
    // existsBy methods
    boolean existsBySectorCode(String sectorCode);
    boolean existsBySectorName(String sectorName);
    boolean existsByDistrictId(Long districtId);
    
    // Custom query
    @Query("SELECT s FROM Sector s WHERE s.district.province.provinceCode = :provinceCode")
    List<Sector> findSectorsByProvinceCode(@Param("provinceCode") String provinceCode);
    
    // Pagination
    Page<Sector> findByDistrictId(Long districtId, Pageable pageable);
}

