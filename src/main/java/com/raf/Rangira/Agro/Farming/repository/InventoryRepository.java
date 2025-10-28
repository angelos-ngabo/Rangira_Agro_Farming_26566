package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Inventory;
import com.raf.Rangira.Agro.Farming.enums.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Inventory Repository
 * Core entity for crop storage tracking
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // findBy methods
    Optional<Inventory> findByInventoryCode(String inventoryCode);
    List<Inventory> findByFarmerId(Long farmerId);
    List<Inventory> findByWarehouseId(Long warehouseId);
    List<Inventory> findByCropTypeId(Long cropTypeId);
    List<Inventory> findByStorekeeperId(Long storekeeperId);
    List<Inventory> findByStatus(InventoryStatus status);
    List<Inventory> findByQualityGrade(String qualityGrade);
    
    // Combined filters
    List<Inventory> findByWarehouseIdAndStatus(Long warehouseId, InventoryStatus status);
    List<Inventory> findByFarmerIdAndStatus(Long farmerId, InventoryStatus status);
    List<Inventory> findByCropTypeIdAndStatus(Long cropTypeId, InventoryStatus status);
    List<Inventory> findByWarehouseIdAndCropTypeIdAndStatus(Long warehouseId, Long cropTypeId, InventoryStatus status);
    
    // Date-based queries
    List<Inventory> findByStorageDateBetween(LocalDate startDate, LocalDate endDate);
    List<Inventory> findByStorageDateAfter(LocalDate date);
    List<Inventory> findByExpectedWithdrawalDateBefore(LocalDate date);
    
    // Quantity-based queries
    List<Inventory> findByRemainingQuantityKgGreaterThan(BigDecimal quantity);
    List<Inventory> findByRemainingQuantityKgBetween(BigDecimal minQty, BigDecimal maxQty);
    
    // existsBy methods
    boolean existsByInventoryCode(String inventoryCode);
    boolean existsByFarmerIdAndStatus(Long farmerId, InventoryStatus status);
    boolean existsByWarehouseIdAndStatus(Long warehouseId, InventoryStatus status);
    
    // Custom queries - Get inventory by province (demonstrates deep relationship)
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.village.cell.sector.district.province.provinceCode = :provinceCode")
    List<Inventory> findInventoriesByProvinceCode(@Param("provinceCode") String provinceCode);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.village.cell.sector.district.province.provinceCode = :provinceCode AND i.status = :status")
    List<Inventory> findInventoriesByProvinceCodeAndStatus(@Param("provinceCode") String provinceCode, 
                                                             @Param("status") InventoryStatus status);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.status = :status AND i.remainingQuantityKg > :minQuantity")
    List<Inventory> findAvailableInventoryInWarehouse(@Param("warehouseId") Long warehouseId, 
                                                        @Param("status") InventoryStatus status,
                                                        @Param("minQuantity") BigDecimal minQuantity);
    
    // Aggregate queries
    @Query("SELECT SUM(i.remainingQuantityKg) FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.status = 'STORED'")
    BigDecimal getTotalStoredQuantityInWarehouse(@Param("warehouseId") Long warehouseId);
    
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.farmer.id = :farmerId AND i.status = 'STORED'")
    long countActiveInventoriesByFarmer(@Param("farmerId") Long farmerId);
    
    // Pagination
    Page<Inventory> findByFarmerId(Long farmerId, Pageable pageable);
    Page<Inventory> findByWarehouseId(Long warehouseId, Pageable pageable);
    Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable);
}

