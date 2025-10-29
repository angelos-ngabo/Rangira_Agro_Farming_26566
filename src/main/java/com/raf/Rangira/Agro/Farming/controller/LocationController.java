package com.raf.Rangira.Agro.Farming.controller;

import com.raf.Rangira.Agro.Farming.entity.Location;
import com.raf.Rangira.Agro.Farming.enums.LocationLevel;
import com.raf.Rangira.Agro.Farming.service.LocationService;
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
public class LocationController {
    
    private final LocationService locationService;
    
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        return new ResponseEntity<>(locationService.createLocation(location), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Location> getLocationByCode(@PathVariable String code) {
        return ResponseEntity.ok(locationService.getLocationByCode(code));
    }
    
    @GetMapping("/provinces")
    public ResponseEntity<List<Location>> getProvinces() {
        return ResponseEntity.ok(locationService.getProvinces());
    }
    
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Location>> getLocationsByLevel(@PathVariable LocationLevel level) {
        return ResponseEntity.ok(locationService.getLocationsByLevel(level));
    }
    
    @GetMapping("/level/{level}/sorted")
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
    public ResponseEntity<Page<Location>> getLocationsByLevelPaginated(
            @PathVariable LocationLevel level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(locationService.getLocationsByLevel(level, pageRequest));
    }
    
    @GetMapping("/parent/{parentId}/children")
    public ResponseEntity<List<Location>> getChildLocations(@PathVariable Long parentId) {
        return ResponseEntity.ok(locationService.getChildLocations(parentId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        return ResponseEntity.ok(locationService.updateLocation(id, location));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

