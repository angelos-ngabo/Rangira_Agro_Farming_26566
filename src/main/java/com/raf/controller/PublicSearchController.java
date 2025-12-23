package com.raf.controller;

import com.raf.service.PublicSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/search")
@RequiredArgsConstructor
@Tag(name = "Public Search", description = "Public search APIs for index/landing pages")
public class PublicSearchController {

private final PublicSearchService publicSearchService;

@GetMapping
@Operation(summary = "Search public data", description = "Search warehouses (public access). Crop types and inventory require authentication.")
public ResponseEntity<Map<String, Object>> search(
@RequestParam String q,
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size) {

Map<String, Object> results = publicSearchService.search(q, page, size);
return ResponseEntity.ok(results);
}
}

