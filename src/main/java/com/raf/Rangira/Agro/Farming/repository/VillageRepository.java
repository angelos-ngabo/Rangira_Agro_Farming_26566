package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Village;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Village Repository
 */
@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {
    
    // findBy methods
    Optional<Village> findByVillageCode(String villageCode);
    Optional<Village> findByVillageName(String villageName);
    List<Village> findByVillageNameContainingIgnoreCase(String name);
    
    // Find by cell
    List<Village> findByCellId(Long cellId);
    List<Village> findByCellCellCode(String cellCode);
    
    // Find by sector (nested)
    List<Village> findByCellSectorId(Long sectorId);
    
    // Find by district (deep nested)
    List<Village> findByCellSectorDistrictId(Long districtId);
    
    // Find by province (deepest nested relationship)
    List<Village> findByCellSectorDistrictProvinceId(Long provinceId);
    List<Village> findByCellSectorDistrictProvinceProvinceCode(String provinceCode);
    
    // existsBy methods
    boolean existsByVillageCode(String villageCode);
    boolean existsByVillageName(String villageName);
    boolean existsByCellId(Long cellId);
    
    // Custom queries
    @Query("SELECT v FROM Village v WHERE v.cell.sector.district.province.provinceCode = :provinceCode")
    List<Village> findVillagesByProvinceCode(@Param("provinceCode") String provinceCode);
    
    @Query("SELECT v FROM Village v JOIN v.cell c JOIN c.sector s JOIN s.district d WHERE d.districtCode = :districtCode")
    List<Village> findVillagesByDistrictCode(@Param("districtCode") String districtCode);
    
    // Pagination
    Page<Village> findByCellId(Long cellId, Pageable pageable);
}

