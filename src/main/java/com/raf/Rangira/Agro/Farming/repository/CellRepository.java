package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Cell;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Cell Repository
 */
@Repository
public interface CellRepository extends JpaRepository<Cell, Long> {
    
    // findBy methods
    Optional<Cell> findByCellCode(String cellCode);
    Optional<Cell> findByCellName(String cellName);
    List<Cell> findByCellNameContainingIgnoreCase(String name);
    
    // Find by sector
    List<Cell> findBySectorId(Long sectorId);
    List<Cell> findBySectorSectorCode(String sectorCode);
    
    // Find by district (nested)
    List<Cell> findBySectorDistrictId(Long districtId);
    
    // Find by province (deep nested)
    List<Cell> findBySectorDistrictProvinceId(Long provinceId);
    
    // existsBy methods
    boolean existsByCellCode(String cellCode);
    boolean existsByCellName(String cellName);
    boolean existsBySectorId(Long sectorId);
    
    // Custom query
    @Query("SELECT c FROM Cell c WHERE c.sector.district.province.provinceCode = :provinceCode")
    List<Cell> findCellsByProvinceCode(@Param("provinceCode") String provinceCode);
    
    // Pagination
    Page<Cell> findBySectorId(Long sectorId, Pageable pageable);
}

