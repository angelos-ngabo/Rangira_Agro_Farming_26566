package com.raf.repository;

import com.raf.entity.User;
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

List<User> findByLocationId(java.util.UUID locationId);

@Query("SELECT u FROM User u WHERE u.location.code = :locationCode")
List<User> findByLocationCode(@Param("locationCode") String locationCode);

@Query("SELECT u FROM User u WHERE u.location.province = :provinceName")
List<User> findUsersInProvince(@Param("provinceName") String provinceName);

@Query("SELECT u FROM User u WHERE u.location.province = :province")
List<User> findUsersByProvinceCode(@Param("province") String province);

@Query("SELECT u FROM User u WHERE u.userType = :userType AND u.location.code = :locationCode")
List<User> findByUserTypeAndLocationCode(@Param("userType") UserType userType, @Param("locationCode") String locationCode);

boolean existsByUserCode(String userCode);
boolean existsByEmail(String email);
boolean existsByPhoneNumber(String phoneNumber);
boolean existsByUserTypeAndStatus(UserType userType, UserStatus status);

@Query("SELECT COUNT(u) FROM User u WHERE u.location.code = :locationCode")
long countByLocationCode(@Param("locationCode") String locationCode);

Page<User> findByUserType(UserType userType, Pageable pageable);
Page<User> findByLocationId(java.util.UUID locationId, Pageable pageable);

List<User> findByUserType(UserType userType, org.springframework.data.domain.Sort sort);
List<User> findByStatus(UserStatus status, org.springframework.data.domain.Sort sort);
List<User> findByUserTypeAndStatus(UserType userType, UserStatus status, org.springframework.data.domain.Sort sort);
}
