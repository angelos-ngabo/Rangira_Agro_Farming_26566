package com.raf.repository;

import com.raf.entity.Location;
import com.raf.enums.ELocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

Optional<Location> findByCode(String code);

boolean existsByCode(String code);

List<Location> findByNameContainingIgnoreCase(String name);

List<Location> findByType(ELocation type);

List<Location> findByType(ELocation type, Sort sort);

Page<Location> findByType(ELocation type, Pageable pageable);

List<Location> findByParentId(UUID parentId);

List<Location> findByParentIsNull();

@Query("SELECT l FROM Location l WHERE l.type = :type AND l.parent.code = :parentCode")
List<Location> findByTypeAndParentCode(@Param("type") ELocation type, @Param("parentCode") String parentCode);

@Query("SELECT l FROM Location l WHERE l.name LIKE %:name%")
List<Location> searchByName(@Param("name") String name);

@Query("SELECT DISTINCT l FROM Location l LEFT JOIN FETCH l.parent p LEFT JOIN FETCH p.parent WHERE l.type = :type")
List<Location> findByTypeWithParents(@Param("type") ELocation type);
}