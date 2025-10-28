package com.raf.Rangira.Agro.Farming.repository;

import com.raf.Rangira.Agro.Farming.entity.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Province Repository
 * Demonstrates: findBy, existsBy, Sorting, Pagination
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    
    // findBy methods
    Optional<Province> findByProvinceCode(String provinceCode);
    Optional<Province> findByProvinceName(String provinceName);
    List<Province> findByProvinceNameContainingIgnoreCase(String name);
    
    // existsBy methods
    boolean existsByProvinceCode(String provinceCode);
    boolean existsByProvinceName(String provinceName);
    
    // Sorting - already available via JpaRepository.findAll(Sort sort)
    // Example usage: provinceRepository.findAll(Sort.by("provinceName").ascending())
    
    // Pagination - already available via JpaRepository.findAll(Pageable pageable)
    // Example usage: provinceRepository.findAll(PageRequest.of(0, 10))
    
    // Custom pagination with sorting
    Page<Province> findAllByOrderByProvinceNameAsc(Pageable pageable);
}

