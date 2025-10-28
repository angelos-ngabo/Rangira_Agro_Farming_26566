package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.entity.Province;
import com.raf.Rangira.Agro.Farming.service.ProvinceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Province REST Controller
 * Demonstrates: CRUD operations, Sorting, Pagination
 */
@RestController
@RequestMapping("/api/provinces")
@RequiredArgsConstructor
@Tag(name = "Province", description = "Province management APIs")
public class ProvinceController {
    
    private final ProvinceService provinceService;
    
    @PostMapping
    @Operation(summary = "Create a new province")
    public ResponseEntity<Province> createProvince(@Valid @RequestBody Province province) {
        Province createdProvince = provinceService.createProvince(province);
        return new ResponseEntity<>(createdProvince, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all provinces")
    public ResponseEntity<List<Province>> getAllProvinces() {
        List<Province> provinces = provinceService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }
    
    @GetMapping("/sorted")
    @Operation(summary = "Get all provinces sorted")
    public ResponseEntity<List<Province>> getAllProvincesSorted(
            @RequestParam(defaultValue = "provinceName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        
        List<Province> provinces = provinceService.getAllProvincesSorted(sort);
        return ResponseEntity.ok(provinces);
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Get provinces with pagination")
    public ResponseEntity<Page<Province>> getAllProvincesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "provinceName") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Province> provinces = provinceService.getAllProvincesPaginated(pageRequest);
        return ResponseEntity.ok(provinces);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get province by ID")
    public ResponseEntity<Province> getProvinceById(@PathVariable Long id) {
        Province province = provinceService.getProvinceById(id);
        return ResponseEntity.ok(province);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get province by code")
    public ResponseEntity<Province> getProvinceByCode(@PathVariable String code) {
        Province province = provinceService.getProvinceByCode(code);
        return ResponseEntity.ok(province);
    }
    
    @GetMapping("/name/{name}")
    @Operation(summary = "Get province by name")
    public ResponseEntity<Province> getProvinceByName(@PathVariable String name) {
        Province province = provinceService.getProvinceByName(name);
        return ResponseEntity.ok(province);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search provinces by name")
    public ResponseEntity<List<Province>> searchProvinces(@RequestParam String term) {
        List<Province> provinces = provinceService.searchProvinces(term);
        return ResponseEntity.ok(provinces);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update province")
    public ResponseEntity<Province> updateProvince(
            @PathVariable Long id,
            @Valid @RequestBody Province province) {
        Province updatedProvince = provinceService.updateProvince(id, province);
        return ResponseEntity.ok(updatedProvince);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete province")
    public ResponseEntity<Void> deleteProvince(@PathVariable Long id) {
        provinceService.deleteProvince(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/exists/{code}")
    @Operation(summary = "Check if province exists by code")
    public ResponseEntity<Boolean> provinceExists(@PathVariable String code) {
        boolean exists = provinceService.provinceExists(code);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get total number of provinces")
    public ResponseEntity<Long> getTotalProvinces() {
        long count = provinceService.getTotalProvinces();
        return ResponseEntity.ok(count);
    }
}

