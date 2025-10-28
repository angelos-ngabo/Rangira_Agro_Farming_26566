package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.Province;
import com.raf.Rangira.Agro.Farming.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Province Service
 * Business logic for Province operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProvinceService {
    
    private final ProvinceRepository provinceRepository;
    
    // CREATE
    public Province createProvince(Province province) {
        log.info("Creating province: {}", province.getProvinceName());
        
        if (provinceRepository.existsByProvinceCode(province.getProvinceCode())) {
            throw new RuntimeException("Province with code " + province.getProvinceCode() + " already exists");
        }
        
        if (provinceRepository.existsByProvinceName(province.getProvinceName())) {
            throw new RuntimeException("Province with name " + province.getProvinceName() + " already exists");
        }
        
        return provinceRepository.save(province);
    }
    
    // READ
    public Province getProvinceById(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Province not found with id: " + id));
    }
    
    public Province getProvinceByCode(String code) {
        return provinceRepository.findByProvinceCode(code)
                .orElseThrow(() -> new RuntimeException("Province not found with code: " + code));
    }
    
    public Province getProvinceByName(String name) {
        return provinceRepository.findByProvinceName(name)
                .orElseThrow(() -> new RuntimeException("Province not found with name: " + name));
    }
    
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }
    
    public List<Province> getAllProvincesSorted(Sort sort) {
        return provinceRepository.findAll(sort);
    }
    
    public Page<Province> getAllProvincesPaginated(Pageable pageable) {
        return provinceRepository.findAll(pageable);
    }
    
    public List<Province> searchProvinces(String searchTerm) {
        return provinceRepository.findByProvinceNameContainingIgnoreCase(searchTerm);
    }
    
    // UPDATE
    public Province updateProvince(Long id, Province provinceDetails) {
        log.info("Updating province with id: {}", id);
        
        Province province = getProvinceById(id);
        
        if (!province.getProvinceCode().equals(provinceDetails.getProvinceCode()) && 
            provinceRepository.existsByProvinceCode(provinceDetails.getProvinceCode())) {
            throw new RuntimeException("Province code already exists");
        }
        
        province.setProvinceCode(provinceDetails.getProvinceCode());
        province.setProvinceName(provinceDetails.getProvinceName());
        
        return provinceRepository.save(province);
    }
    
    // DELETE
    public void deleteProvince(Long id) {
        log.info("Deleting province with id: {}", id);
        
        Province province = getProvinceById(id);
        provinceRepository.delete(province);
    }
    
    // Business logic
    public boolean provinceExists(String code) {
        return provinceRepository.existsByProvinceCode(code);
    }
    
    public long getTotalProvinces() {
        return provinceRepository.count();
    }
}

