package com.raf.repository;

import com.raf.entity.WarehouseAccess;
import com.raf.enums.AccessLevel;
import com.raf.enums.WarehouseAccessStatus;
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
List<WarehouseAccess> findByWarehouseIdAndAccessLevelAndIsActive(Long warehouseId, AccessLevel accessLevel, Boolean isActive);

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

List<WarehouseAccess> findByStatus(WarehouseAccessStatus status);
List<WarehouseAccess> findByWarehouseIdAndStatus(Long warehouseId, WarehouseAccessStatus status);
List<WarehouseAccess> findByUserIdAndStatus(Long userId, WarehouseAccessStatus status);
List<WarehouseAccess> findByWarehouseIdAndStatusAndIsActive(Long warehouseId, WarehouseAccessStatus status, Boolean isActive);

@Query("SELECT wa FROM WarehouseAccess wa JOIN FETCH wa.user JOIN FETCH wa.warehouse WHERE wa.accessLevel = :accessLevel AND wa.isActive = :isActive")
List<WarehouseAccess> findByAccessLevelAndIsActiveWithUserAndWarehouse(@Param("accessLevel") AccessLevel accessLevel, @Param("isActive") Boolean isActive);

@Query("SELECT wa FROM WarehouseAccess wa JOIN FETCH wa.user JOIN FETCH wa.warehouse WHERE wa.isActive = :isActive")
List<WarehouseAccess> findAllActiveWithUserAndWarehouse(@Param("isActive") Boolean isActive);

@Query("SELECT wa FROM WarehouseAccess wa JOIN FETCH wa.user JOIN FETCH wa.warehouse")
List<WarehouseAccess> findAllWithUserAndWarehouse();

@Query("SELECT wa FROM WarehouseAccess wa JOIN FETCH wa.user JOIN FETCH wa.warehouse WHERE wa.id = :id")
Optional<WarehouseAccess> findByIdWithUserAndWarehouse(@Param("id") Long id);

@Query("SELECT wa FROM WarehouseAccess wa JOIN FETCH wa.user JOIN FETCH wa.warehouse WHERE wa.user.id = :userId AND wa.warehouse.id = :warehouseId")
Optional<WarehouseAccess> findByUserIdAndWarehouseIdWithUserAndWarehouse(@Param("userId") Long userId, @Param("warehouseId") Long warehouseId);
}

