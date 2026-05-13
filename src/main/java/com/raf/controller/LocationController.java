package com.raf.controller;

import com.raf.entity.Location;
import com.raf.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Location hierarchy management APIs (Province, District, Sector, Cell, Village)")
public class LocationController {
private final LocationService locationService;

/**
 * Some clients call {@code GET /api/locations} expecting the province list (see legacy API docs).
 * Same payload as {@link #getProvinces()}.
 */
@GetMapping({"", "/"})
@Operation(summary = "Get all provinces (root alias)")
public ResponseEntity<List<String>> getProvincesRoot() {
return ResponseEntity.ok(locationService.getProvinces());
}

/** Clients that call {@code GET /api/locations/type} without a level segment. */
@GetMapping("/type")
@Operation(summary = "Legacy path without level (same as provinces)")
public ResponseEntity<List<String>> getLocationsTypeRoot() {
return ResponseEntity.ok(locationService.getProvinces());
}

/**
 * Frontend compatibility: calls such as {@code GET /api/locations/type/Province} (see API_MAPPING).
 */
@GetMapping("/type/{level}")
@Operation(summary = "Locations by hierarchy level (legacy path)")
public ResponseEntity<Object> getLocationsByHierarchyLevel(
@PathVariable String level,
@RequestParam(required = false) String province,
@RequestParam(required = false) String district,
@RequestParam(required = false) String sector,
@RequestParam(required = false) String cell
) {
String l = level == null ? "" : level.trim().toLowerCase(Locale.ROOT);
return switch (l) {
case "province", "provinces" -> ResponseEntity.ok(locationService.getProvinces());
case "district", "districts" -> {
if (province == null || province.isBlank()) {
yield ResponseEntity.badRequest().body(Map.of("error", "province query parameter is required for district"));
}
yield ResponseEntity.ok(locationService.getDistricts(province));
}
case "sector", "sectors" -> {
if (province == null || province.isBlank() || district == null || district.isBlank()) {
yield ResponseEntity.badRequest().body(Map.of("error", "province and district query parameters are required for sector"));
}
yield ResponseEntity.ok(locationService.getSectors(province, district));
}
case "cell", "cells" -> {
if (province == null || province.isBlank() || district == null || district.isBlank()
|| sector == null || sector.isBlank()) {
yield ResponseEntity.badRequest().body(Map.of("error", "province, district, and sector query parameters are required for cell"));
}
yield ResponseEntity.ok(locationService.getCells(province, district, sector));
}
case "village", "villages" -> {
if (province == null || province.isBlank() || district == null || district.isBlank()
|| sector == null || sector.isBlank() || cell == null || cell.isBlank()) {
yield ResponseEntity.badRequest().body(Map.of("error", "province, district, sector, and cell query parameters are required for village"));
}
yield ResponseEntity.ok(locationService.getVillages(province, district, sector, cell));
}
default -> ResponseEntity.badRequest().body(Map.of(
"error",
"Unsupported location level: " + level + ". Use Province, District, Sector, Cell, or Village."));
};
}

@GetMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
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
@Operation(summary = "Get all provinces")
public ResponseEntity<List<String>> getProvinces() {
return ResponseEntity.ok(locationService.getProvinces());
}

@GetMapping("/districts")
@Operation(summary = "Get districts for a province")
public ResponseEntity<List<String>> getDistricts(@RequestParam String province) {
return ResponseEntity.ok(locationService.getDistricts(province));
}

@GetMapping("/sectors")
@Operation(summary = "Get sectors for a district")
public ResponseEntity<List<String>> getSectors(@RequestParam String province, @RequestParam String district) {
return ResponseEntity.ok(locationService.getSectors(province, district));
}

@GetMapping("/cells")
@Operation(summary = "Get cells for a sector")
public ResponseEntity<List<String>> getCells(@RequestParam String province, @RequestParam String district, @RequestParam String sector) {
return ResponseEntity.ok(locationService.getCells(province, district, sector));
}

@GetMapping("/villages")
@Operation(summary = "Get villages for a cell")
public ResponseEntity<List<Location>> getVillages(@RequestParam String province, @RequestParam String district, @RequestParam String sector, @RequestParam String cell) {
return ResponseEntity.ok(locationService.getVillages(province, district, sector, cell));
}

@GetMapping("/search")
@Operation(summary = "Search locations by village name")
public ResponseEntity<List<Location>> searchLocations(@RequestParam String name) {
return ResponseEntity.ok(locationService.searchByName(name));
}
}

