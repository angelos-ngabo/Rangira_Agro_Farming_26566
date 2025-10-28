package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.enums.UserStatus;
import com.raf.Rangira.Agro.Farming.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * IMPORTANT: Demonstrates User-Location relationship queries
 * REQUIREMENT: Get users by province code/name
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic findBy methods
    Optional<User> findByUserCode(String userCode);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Find by user type
    List<User> findByUserType(UserType userType);
    List<User> findByUserTypeAndStatus(UserType userType, UserStatus status);
    
    // Find by status
    List<User> findByStatus(UserStatus status);
    
    // ============================================
    // REQUIREMENT: User-Location Relationship
    // Get users by province code or province name
    // ============================================
    
    // Get users by village
    List<User> findByVillageId(Long villageId);
    
    // Get users by cell (nested)
    List<User> findByVillageCellId(Long cellId);
    
    // Get users by sector (deep nested)
    List<User> findByVillageCellSectorId(Long sectorId);
    
    // Get users by district (deeper nested)
    List<User> findByVillageCellSectorDistrictId(Long districtId);
    
    // Get users by PROVINCE ID (deepest nested) - REQUIREMENT
    List<User> findByVillageCellSectorDistrictProvinceId(Long provinceId);
    
    // Get users by PROVINCE CODE - REQUIREMENT
    List<User> findByVillageCellSectorDistrictProvinceProvinceCode(String provinceCode);
    
    // Get users by PROVINCE NAME - REQUIREMENT
    List<User> findByVillageCellSectorDistrictProvinceProvinceName(String provinceName);
    
    // existsBy methods
    boolean existsByUserCode(String userCode);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserTypeAndStatus(UserType userType, UserStatus status);
    
    // Custom queries
    @Query("SELECT u FROM User u WHERE u.village.cell.sector.district.province.provinceCode = :provinceCode AND u.userType = :userType")
    List<User> findUsersByProvinceCodeAndUserType(@Param("provinceCode") String provinceCode, @Param("userType") UserType userType);
    
    @Query("SELECT u FROM User u WHERE u.village.cell.sector.district.province.id = :provinceId AND u.status = :status")
    List<User> findUsersByProvinceIdAndStatus(@Param("provinceId") Long provinceId, @Param("status") UserStatus status);
    
    // Count queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.village.cell.sector.district.province.provinceCode = :provinceCode")
    long countUsersByProvinceCode(@Param("provinceCode") String provinceCode);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND u.status = :status")
    long countByUserTypeAndStatus(@Param("userType") UserType userType, @Param("status") UserStatus status);
    
    // Pagination
    Page<User> findByUserType(UserType userType, Pageable pageable);
    Page<User> findByVillageCellSectorDistrictProvinceProvinceCode(String provinceCode, Pageable pageable);
}

