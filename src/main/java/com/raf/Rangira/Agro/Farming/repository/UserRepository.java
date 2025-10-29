package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.User;
import com.raf.Rangira.Agro.Farming.enums.LocationLevel;
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

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserCode(String userCode);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    List<User> findByUserType(UserType userType);
    List<User> findByUserTypeAndStatus(UserType userType, UserStatus status);
    List<User> findByStatus(UserStatus status);
    
    List<User> findByLocationId(Long locationId);
    
    @Query("SELECT u FROM User u WHERE " +
           "u.location.code = :locationCode OR " +
           "u.location.parent.code = :locationCode OR " +
           "u.location.parent.parent.code = :locationCode OR " +
           "u.location.parent.parent.parent.code = :locationCode OR " +
           "u.location.parent.parent.parent.parent.code = :locationCode")
    List<User> findByLocationCode(@Param("locationCode") String locationCode);
    
    @Query("SELECT u FROM User u WHERE " +
           "u.location = :location OR " +
           "u.location.parent = :location OR " +
           "u.location.parent.parent = :location OR " +
           "u.location.parent.parent.parent = :location OR " +
           "u.location.parent.parent.parent.parent = :location")
    List<User> findByLocationOrAnyParent(@Param("location") com.raf.Rangira.Agro.Farming.entity.Location location);
    
    @Query("SELECT u FROM User u WHERE u.location.level = :level AND u.location.code = :code")
    List<User> findByLocationLevelAndCode(@Param("level") LocationLevel level, @Param("code") String code);
    
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.location.code = :locationCode")
    List<User> findByUserTypeAndLocationCode(@Param("userType") UserType userType, @Param("locationCode") String locationCode);
    
    boolean existsByUserCode(String userCode);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserTypeAndStatus(UserType userType, UserStatus status);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.location.code = :locationCode")
    long countByLocationCode(@Param("locationCode") String locationCode);
    
    Page<User> findByUserType(UserType userType, Pageable pageable);
    Page<User> findByLocationId(Long locationId, Pageable pageable);
}
