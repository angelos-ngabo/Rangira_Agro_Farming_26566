package com.raf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check endpoint")
public class HealthController {

@GetMapping
@Operation(summary = "Health check", description = "Check if the API is running")
public ResponseEntity<Map<String, Object>> health() {
Map<String, Object> response = new HashMap<>();
response.put("status", "UP");
response.put("message", "Rangira Agro Farming API is running");
response.put("timestamp", System.currentTimeMillis());
return ResponseEntity.ok(response);
}
}

