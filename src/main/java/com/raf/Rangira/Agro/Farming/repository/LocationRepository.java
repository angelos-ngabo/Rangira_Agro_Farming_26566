package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Location;
import com.raf.Rangira.Agro.Farming.enums.LocationLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Location> findByNameContainingIgnoreCase(String name);
    
    List<Location> findByLevel(LocationLevel level);
    
    List<Location> findByLevel(LocationLevel level, Sort sort);
    
    Page<Location> findByLevel(LocationLevel level, Pageable pageable);
    
    List<Location> findByParentId(Long parentId);
    
    List<Location> findByParentIsNull();
    
    @Query("SELECT l FROM Location l WHERE l.level = :level AND l.parent.code = :parentCode")
    List<Location> findByLevelAndParentCode(@Param("level") LocationLevel level, @Param("parentCode") String parentCode);
    
    @Query("SELECT l FROM Location l WHERE l.name LIKE %:name%")
    List<Location> searchByName(@Param("name") String name);
}

