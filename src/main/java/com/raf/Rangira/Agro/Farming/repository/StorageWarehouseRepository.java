package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.StorageWarehouse;
import com.raf.Rangira.Agro.Farming.enums.WarehouseStatus;
import com.raf.Rangira.Agro.Farming.enums.WarehouseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * StorageWarehouse Repository
 */
@Repository
public interface StorageWarehouseRepository extends JpaRepository<StorageWarehouse, Long> {
    
    // findBy methods
    Optional<StorageWarehouse> findByWarehouseCode(String warehouseCode);
    List<StorageWarehouse> findByWarehouseName(String warehouseName);
    List<StorageWarehouse> findByWarehouseNameContainingIgnoreCase(String name);
    List<StorageWarehouse> findByWarehouseType(WarehouseType warehouseType);
    List<StorageWarehouse> findByStatus(WarehouseStatus status);
    List<StorageWarehouse> findByWarehouseTypeAndStatus(WarehouseType type, WarehouseStatus status);
    
    // Find by location
    List<StorageWarehouse> findByVillageId(Long villageId);
    List<StorageWarehouse> findByVillageCellSectorDistrictProvinceProvinceCode(String provinceCode);
    
    // Find by capacity
    List<StorageWarehouse> findByAvailableCapacityKgGreaterThan(BigDecimal capacity);
    
    // existsBy methods
    boolean existsByWarehouseCode(String warehouseCode);
    boolean existsByWarehouseName(String warehouseName);
    boolean existsByWarehouseTypeAndStatus(WarehouseType type, WarehouseStatus status);
    boolean existsByVillageId(Long villageId);
    
    // Custom queries
    @Query("SELECT w FROM StorageWarehouse w WHERE w.status = :status AND w.availableCapacityKg > :minCapacity")
    List<StorageWarehouse> findActiveWarehousesWithCapacity(@Param("status") WarehouseStatus status, 
                                                              @Param("minCapacity") BigDecimal minCapacity);
    
    @Query("SELECT w FROM StorageWarehouse w WHERE w.village.cell.sector.district.province.provinceCode = :provinceCode AND w.status = :status")
    List<StorageWarehouse> findWarehousesByProvinceCodeAndStatus(@Param("provinceCode") String provinceCode, 
                                                                   @Param("status") WarehouseStatus status);
    
    // Count queries
    @Query("SELECT COUNT(w) FROM StorageWarehouse w WHERE w.warehouseType = :type AND w.status = :status")
    long countByWarehouseTypeAndStatus(@Param("type") WarehouseType type, @Param("status") WarehouseStatus status);
    
    // Pagination
    Page<StorageWarehouse> findByStatus(WarehouseStatus status, Pageable pageable);
    Page<StorageWarehouse> findByWarehouseType(WarehouseType type, Pageable pageable);
}

