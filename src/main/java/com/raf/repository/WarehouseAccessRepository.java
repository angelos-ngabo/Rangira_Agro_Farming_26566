package com.raf.repository;

import com.raf.entity.WarehouseAccess;
import com.raf.enums.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseAccessRepository extends JpaRepository<WarehouseAccess, Long> {
    
    List<WarehouseAccess> findByUserId(Long userId);
    List<WarehouseAccess> findByWarehouseId(Long warehouseId);
    Optional<WarehouseAccess> findByUserIdAndWarehouseId(Long userId, Long warehouseId);
    List<WarehouseAccess> findByAccessLevel(AccessLevel accessLevel);
    List<WarehouseAccess> findByIsActive(Boolean isActive);
    List<WarehouseAccess> findByUserIdAndIsActive(Long userId, Boolean isActive);
    List<WarehouseAccess> findByWarehouseIdAndIsActive(Long warehouseId, Boolean isActive);
    
    List<WarehouseAccess> findByAccessLevelAndIsActive(AccessLevel accessLevel, Boolean isActive);
    List<WarehouseAccess> findByUserIdAndAccessLevel(Long userId, AccessLevel accessLevel);
    
    List<WarehouseAccess> findByExpiryDateBefore(LocalDate date);
    List<WarehouseAccess> findByExpiryDateBeforeAndIsActive(LocalDate date, Boolean isActive);
    
    boolean existsByUserIdAndWarehouseId(Long userId, Long warehouseId);
    boolean existsByUserIdAndWarehouseIdAndIsActive(Long userId, Long warehouseId, Boolean isActive);
    boolean existsByUserIdAndAccessLevel(Long userId, AccessLevel accessLevel);
    
    @Query("SELECT wa FROM WarehouseAccess wa WHERE wa.user.id = :userId AND wa.warehouse.id = :warehouseId AND wa.isActive = true")
    Optional<WarehouseAccess> findActiveAccessByUserAndWarehouse(@Param("userId") Long userId, 
                                                                   @Param("warehouseId") Long warehouseId);
    
    @Query("SELECT wa FROM WarehouseAccess wa WHERE wa.user.id = :userId AND (wa.expiryDate IS NULL OR wa.expiryDate > :currentDate) AND wa.isActive = true")
    List<WarehouseAccess> findValidAccessForUser(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT COUNT(wa) FROM WarehouseAccess wa WHERE wa.warehouse.id = :warehouseId AND wa.isActive = true")
    long countActiveAccessByWarehouse(@Param("warehouseId") Long warehouseId);
    
    Page<WarehouseAccess> findByUserId(Long userId, Pageable pageable);
    Page<WarehouseAccess> findByWarehouseId(Long warehouseId, Pageable pageable);
}

