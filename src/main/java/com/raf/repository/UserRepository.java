package com.raf.repository;

import com.raf.entity.User;
import com.raf.enums.LocationLevel;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
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
    
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN u.location l1 " +
           "LEFT JOIN l1.parent l2 " +
           "LEFT JOIN l2.parent l3 " +
           "LEFT JOIN l3.parent l4 " +
           "LEFT JOIN l4.parent l5 " +
           "WHERE l1.code = :locationCode OR " +
           "l2.code = :locationCode OR " +
           "l3.code = :locationCode OR " +
           "l4.code = :locationCode OR " +
           "l5.code = :locationCode")
    List<User> findByLocationCode(@Param("locationCode") String locationCode);
    
    @Query("SELECT u FROM User u WHERE " +
           "u.location = :location OR " +
           "u.location.parent = :location OR " +
           "u.location.parent.parent = :location OR " +
           "u.location.parent.parent.parent = :location OR " +
           "u.location.parent.parent.parent.parent = :location")
    List<User> findByLocationOrAnyParent(@Param("location") com.raf.entity.Location location);
    
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
