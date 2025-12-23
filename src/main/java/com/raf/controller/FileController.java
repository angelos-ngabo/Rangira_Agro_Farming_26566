package com.raf.controller;

import com.raf.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "File serving APIs")
public class FileController {

private final FileStorageService fileStorageService;

@GetMapping("/profile-pictures/{filename:.+}")
@Operation(summary = "Get profile picture", description = "Retrieve a user's profile picture")
public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
try {
Path filePath = fileStorageService.getProfilePicturePath(filename);
Resource resource = new UrlResource(filePath.toUri());

if (resource.exists() && resource.isReadable()) {
String contentType = Files.probeContentType(filePath);
if (contentType == null) {
contentType = "application/octet-stream";
}

return ResponseEntity.ok()
.contentType(MediaType.parseMediaType(contentType))
.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
.body(resource);
} else {
return ResponseEntity.notFound().build();
}
} catch (Exception e) {
return ResponseEntity.notFound().build();
}
}

@GetMapping("/crop-images/{filename:.+}")
@Operation(summary = "Get crop image", description = "Retrieve a crop type's image")
public ResponseEntity<Resource> getCropImage(@PathVariable String filename) {
try {
Path filePath = fileStorageService.getCropImagePath(filename);
Resource resource = new UrlResource(filePath.toUri());

if (resource.exists() && resource.isReadable()) {
String contentType = Files.probeContentType(filePath);
if (contentType == null) {
contentType = "application/octet-stream";
}

return ResponseEntity.ok()
.contentType(MediaType.parseMediaType(contentType))
.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
.body(resource);
} else {
return ResponseEntity.notFound().build();
}
} catch (Exception e) {
return ResponseEntity.notFound().build();
}
}

@GetMapping("/warehouse-access-images/{filename:.+}")
@Operation(summary = "Get warehouse access crop image", description = "Retrieve a warehouse access request's crop image")
public ResponseEntity<Resource> getWarehouseAccessImage(@PathVariable String filename) {
try {
Path filePath = fileStorageService.getWarehouseAccessImagePath(filename);
Resource resource = new UrlResource(filePath.toUri());

if (resource.exists() && resource.isReadable()) {
String contentType = Files.probeContentType(filePath);
if (contentType == null) {
contentType = "application/octet-stream";
}

return ResponseEntity.ok()
.contentType(MediaType.parseMediaType(contentType))
.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
.body(resource);
} else {
return ResponseEntity.notFound().build();
}
} catch (Exception e) {
return ResponseEntity.notFound().build();
}
}
}

