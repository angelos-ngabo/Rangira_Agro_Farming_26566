package com.raf.repository;

import com.raf.entity.Inventory;
import com.raf.enums.InventoryStatus;
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

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByInventoryCode(String inventoryCode);
    List<Inventory> findByFarmerId(Long farmerId);
    List<Inventory> findByWarehouseId(Long warehouseId);
    List<Inventory> findByCropTypeId(Long cropTypeId);
    List<Inventory> findByStorekeeperId(Long storekeeperId);
    List<Inventory> findByStatus(InventoryStatus status);
    List<Inventory> findByQualityGrade(String qualityGrade);
    
    List<Inventory> findByWarehouseIdAndStatus(Long warehouseId, InventoryStatus status);
    List<Inventory> findByFarmerIdAndStatus(Long farmerId, InventoryStatus status);
    List<Inventory> findByCropTypeIdAndStatus(Long cropTypeId, InventoryStatus status);
    List<Inventory> findByWarehouseIdAndCropTypeIdAndStatus(Long warehouseId, Long cropTypeId, InventoryStatus status);
    
    List<Inventory> findByStorageDateBetween(LocalDate startDate, LocalDate endDate);
    List<Inventory> findByStorageDateAfter(LocalDate date);
    List<Inventory> findByExpectedWithdrawalDateBefore(LocalDate date);
    
    List<Inventory> findByRemainingQuantityKgGreaterThan(BigDecimal quantity);
    List<Inventory> findByRemainingQuantityKgBetween(BigDecimal minQty, BigDecimal maxQty);
    
    boolean existsByInventoryCode(String inventoryCode);
    boolean existsByFarmerIdAndStatus(Long farmerId, InventoryStatus status);
    boolean existsByWarehouseIdAndStatus(Long warehouseId, InventoryStatus status);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.location.code = :locationCode")
    List<Inventory> findInventoriesByLocationCode(@Param("locationCode") String locationCode);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.location.code = :locationCode AND i.status = :status")
    List<Inventory> findInventoriesByLocationCodeAndStatus(@Param("locationCode") String locationCode, 
                                                             @Param("status") InventoryStatus status);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.status = :status AND i.remainingQuantityKg > :minQuantity")
    List<Inventory> findAvailableInventoryInWarehouse(@Param("warehouseId") Long warehouseId, 
                                                        @Param("status") InventoryStatus status,
                                                        @Param("minQuantity") BigDecimal minQuantity);
    
    @Query("SELECT SUM(i.remainingQuantityKg) FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.status = 'STORED'")
    BigDecimal getTotalStoredQuantityInWarehouse(@Param("warehouseId") Long warehouseId);
    
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.farmer.id = :farmerId AND i.status = 'STORED'")
    long countActiveInventoriesByFarmer(@Param("farmerId") Long farmerId);
    
    Page<Inventory> findByFarmerId(Long farmerId, Pageable pageable);
    Page<Inventory> findByWarehouseId(Long warehouseId, Pageable pageable);
    Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable);
}
