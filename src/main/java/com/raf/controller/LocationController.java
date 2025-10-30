package com.raf.controller;

import com.raf.entity.Location;
import com.raf.enums.LocationLevel;
import com.raf.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Location hierarchy management APIs (Province, District, Sector, Cell, Village)")
public class LocationController {
    private final LocationService locationService;
    
    @PostMapping
    @Operation(summary = "Create a new location")
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        return new ResponseEntity<>(locationService.createLocation(location), HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get location by code")
    public ResponseEntity<Location> getLocationByCode(@PathVariable String code) {
        return ResponseEntity.ok(locationService.getLocationByCode(code));
    }
    
    @GetMapping("/provinces")
    @Operation(summary = "Get all provinces")
    public ResponseEntity<List<Location>> getProvinces() {
        return ResponseEntity.ok(locationService.getProvinces());
    }
    
    @GetMapping("/level/{level}")
    @Operation(summary = "Get locations by level")
    public ResponseEntity<List<Location>> getLocationsByLevel(@PathVariable LocationLevel level) {
        return ResponseEntity.ok(locationService.getLocationsByLevel(level));
    }
    
    @GetMapping("/level/{level}/sorted")
    @Operation(summary = "Get locations by level with sorting")
    public ResponseEntity<List<Location>> getLocationsByLevelSorted(
            @PathVariable LocationLevel level,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(locationService.getLocationsByLevel(level, sort));
    }
    
    @GetMapping("/level/{level}/paginated")
    @Operation(summary = "Get locations by level with pagination")
    public ResponseEntity<Page<Location>> getLocationsByLevelPaginated(
            @PathVariable LocationLevel level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(locationService.getLocationsByLevel(level, pageRequest));
    }
    
    @GetMapping("/parent/{parentId}/children")
    @Operation(summary = "Get child locations")
    public ResponseEntity<List<Location>> getChildLocations(@PathVariable Long parentId) {
        return ResponseEntity.ok(locationService.getChildLocations(parentId));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update location")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        return ResponseEntity.ok(locationService.updateLocation(id, location));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete location")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

