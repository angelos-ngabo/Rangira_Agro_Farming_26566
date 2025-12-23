package com.raf.controller;

import com.raf.entity.Location;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.User;
import com.raf.enums.ELocation;
import com.raf.enums.UserType;
import com.raf.repository.UserRepository;
import com.raf.service.LocationService;
import com.raf.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Location hierarchy management APIs (Province, District, Sector, Cell, Village)")
public class LocationController {
private final LocationService locationService;
private final UserRepository userRepository;
private final WarehouseService warehouseService;

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
public ResponseEntity<Location> getLocationById(@PathVariable UUID id) {
return ResponseEntity.ok(locationService.getLocationById(id));
}

@GetMapping("/code/{code}")
@Operation(summary = "Get location by code")
public ResponseEntity<Location> getLocationByCode(@PathVariable String code) {
return ResponseEntity.ok(locationService.getLocationByCode(code));
}

@GetMapping("/provinces")
@Operation(summary = "Get all provinces (or filtered by user role)")
public ResponseEntity<List<Location>> getProvinces(
@RequestParam(required = false) Boolean filtered) {

if (Boolean.TRUE.equals(filtered)) {
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
if (authentication != null && authentication.isAuthenticated() &&
!authentication.getName().equals("anonymousUser")) {
try {
User user = userRepository.findByEmail(authentication.getName())
.orElse(null);

if (user != null) {
List<StorageWarehouse> warehouses = null;
if (user.getUserType() == UserType.STOREKEEPER) {
warehouses = warehouseService.getWarehousesByStorekeeperId(user.getId());
}
return ResponseEntity.ok(locationService.getFilteredProvinces(user, warehouses));
}
} catch (Exception e) {

return ResponseEntity.ok(locationService.getProvinces());
}
}
}
return ResponseEntity.ok(locationService.getProvinces());
}

@GetMapping("/type/{type}")
@Operation(summary = "Get locations by type")
public ResponseEntity<List<Location>> getLocationsByType(@PathVariable ELocation type) {
return ResponseEntity.ok(locationService.getLocationsByType(type));
}

@GetMapping("/type/{type}/sorted")
@Operation(summary = "Get locations by type with sorting")
public ResponseEntity<List<Location>> getLocationsByTypeSorted(
@PathVariable ELocation type,
@RequestParam(defaultValue = "name") String sortBy,
@RequestParam(defaultValue = "ASC") String direction) {
Sort sort = direction.equalsIgnoreCase("DESC")
? Sort.by(sortBy).descending()
: Sort.by(sortBy).ascending();
return ResponseEntity.ok(locationService.getLocationsByType(type, sort));
}

@GetMapping("/type/{type}/paginated")
@Operation(summary = "Get locations by type with pagination")
public ResponseEntity<Page<Location>> getLocationsByTypePaginated(
@PathVariable ELocation type,
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size,
@RequestParam(defaultValue = "name") String sortBy) {
PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
return ResponseEntity.ok(locationService.getLocationsByType(type, pageRequest));
}

@GetMapping("/parent/{parentId}/children")
@Operation(summary = "Get child locations")
public ResponseEntity<List<Location>> getChildLocations(@PathVariable UUID parentId) {
return ResponseEntity.ok(locationService.getChildLocations(parentId));
}

@GetMapping("/search")
@Operation(summary = "Search locations by name")
public ResponseEntity<List<Location>> searchLocations(@RequestParam String name) {
return ResponseEntity.ok(locationService.searchByName(name));
}

@GetMapping("/parent/{parentId}/children/count")
@Operation(summary = "Get count of child locations for debugging")
public ResponseEntity<Map<String, Object>> getChildLocationsCount(@PathVariable UUID parentId) {
return ResponseEntity.ok(locationService.getChildLocationsCount(parentId));
}

@PutMapping("/{id}")
@Operation(summary = "Update location")
public ResponseEntity<Location> updateLocation(@PathVariable UUID id, @RequestBody Location location) {
return ResponseEntity.ok(locationService.updateLocation(id, location));
}

@DeleteMapping("/{id}")
@Operation(summary = "Delete location")
public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
locationService.deleteLocation(id);
return ResponseEntity.noContent().build();
}
}

