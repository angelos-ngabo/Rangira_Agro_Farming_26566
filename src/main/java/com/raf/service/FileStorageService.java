package com.raf.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

@Value("${app.upload.profile-pictures:uploads/profile-pictures}")
private String profilePicturesDir;

@Value("${app.upload.crop-images:uploads/crop-images}")
private String cropImagesDir;

@Value("${app.upload.warehouse-access-images:uploads/warehouse-access-images}")
private String warehouseAccessImagesDir;

public String storeProfilePicture(MultipartFile file, Long userId) {
try {

if (file.isEmpty()) {
throw new IllegalArgumentException("File is empty");
}


String contentType = file.getContentType();
if (contentType == null || !contentType.startsWith("image/")) {
throw new IllegalArgumentException("File must be an image");
}


if (file.getSize() > 5 * 1024 * 1024) {
throw new IllegalArgumentException("File size must be less than 5MB");
}


Path uploadPath = Paths.get(profilePicturesDir);
if (!Files.exists(uploadPath)) {
Files.createDirectories(uploadPath);
}


String originalFilename = file.getOriginalFilename();
String extension = "";
if (originalFilename != null && originalFilename.contains(".")) {
extension = originalFilename.substring(originalFilename.lastIndexOf("."));
}
String filename = "profile_" + userId + "_" + UUID.randomUUID() + extension;


Path filePath = uploadPath.resolve(filename);
Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


String fileUrl = "/api/files/profile-pictures/" + filename;
log.info("Profile picture saved: {}", fileUrl);

return fileUrl;
} catch (IOException e) {
log.error("Failed to store profile picture for user: {}", userId, e);
throw new RuntimeException("Failed to store profile picture: " + e.getMessage(), e);
}
}

public void deleteProfilePicture(String filename) {
try {
Path filePath = Paths.get(profilePicturesDir).resolve(filename);
if (Files.exists(filePath)) {
Files.delete(filePath);
log.info("Profile picture deleted: {}", filename);
}
} catch (IOException e) {
log.error("Failed to delete profile picture: {}", filename, e);
}
}

public Path getProfilePicturePath(String filename) {
return Paths.get(profilePicturesDir).resolve(filename);
}


public String storeCropImage(MultipartFile file, Long cropTypeId) {
try {

if (file.isEmpty()) {
throw new IllegalArgumentException("File is empty");
}


String contentType = file.getContentType();
if (contentType == null || !contentType.startsWith("image/")) {
throw new IllegalArgumentException("File must be an image");
}


if (file.getSize() > 5 * 1024 * 1024) {
throw new IllegalArgumentException("File size must be less than 5MB");
}


Path uploadPath = Paths.get(cropImagesDir);
if (!Files.exists(uploadPath)) {
Files.createDirectories(uploadPath);
}


String originalFilename = file.getOriginalFilename();
String extension = "";
if (originalFilename != null && originalFilename.contains(".")) {
extension = originalFilename.substring(originalFilename.lastIndexOf("."));
}
String filename = "crop_" + cropTypeId + "_" + UUID.randomUUID() + extension;


Path filePath = uploadPath.resolve(filename);
Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


String fileUrl = "/api/files/crop-images/" + filename;
log.info("Crop image saved: {}", fileUrl);

return fileUrl;
} catch (IOException e) {
log.error("Failed to store crop image for crop type: {}", cropTypeId, e);
throw new RuntimeException("Failed to store crop image: " + e.getMessage(), e);
}
}

public void deleteCropImage(String filename) {
try {
Path filePath = Paths.get(cropImagesDir).resolve(filename);
if (Files.exists(filePath)) {
Files.delete(filePath);
log.info("Crop image deleted: {}", filename);
}
} catch (IOException e) {
log.error("Failed to delete crop image: {}", filename, e);
}
}

public Path getCropImagePath(String filename) {
return Paths.get(cropImagesDir).resolve(filename);
}


public String storeWarehouseAccessImage(MultipartFile file, Long userId) {
try {
log.info("Attempting to store warehouse access image for user: {}", userId);


if (file == null || file.isEmpty()) {
log.warn("File is null or empty for user: {}", userId);
throw new IllegalArgumentException("File is empty");
}


String contentType = file.getContentType();
log.debug("File content type: {}", contentType);
if (contentType == null || !contentType.startsWith("image/")) {
log.warn("Invalid file type: {} for user: {}", contentType, userId);
throw new IllegalArgumentException("File must be an image");
}


long fileSize = file.getSize();
log.debug("File size: {} bytes for user: {}", fileSize, userId);
if (fileSize > 5 * 1024 * 1024) {
log.warn("File size too large: {} bytes for user: {}", fileSize, userId);
throw new IllegalArgumentException("File size must be less than 5MB");
}


Path uploadPath = Paths.get(warehouseAccessImagesDir).toAbsolutePath().normalize();
log.info("Upload path: {}", uploadPath);

try {
if (!Files.exists(uploadPath)) {
log.info("Creating upload directory: {}", uploadPath);
Files.createDirectories(uploadPath);
log.info("Upload directory created successfully");
} else {
log.debug("Upload directory already exists: {}", uploadPath);
}
} catch (IOException e) {
log.error("Failed to create upload directory: {}", uploadPath, e);
throw new RuntimeException("Failed to create upload directory: " + e.getMessage(), e);
}


String originalFilename = file.getOriginalFilename();
String extension = "";
if (originalFilename != null && originalFilename.contains(".")) {
extension = originalFilename.substring(originalFilename.lastIndexOf("."));
}
String filename = "warehouse_access_" + userId + "_" + UUID.randomUUID() + extension;
log.debug("Generated filename: {}", filename);


Path filePath = uploadPath.resolve(filename);
log.info("Saving file to: {}", filePath.toAbsolutePath());
Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
log.info("File saved successfully to: {}", filePath.toAbsolutePath());


String fileUrl = "/api/files/warehouse-access-images/" + filename;
log.info("Warehouse access image saved successfully. URL: {}", fileUrl);

return fileUrl;
} catch (IllegalArgumentException e) {
log.error("Validation error storing warehouse access image for user {}: {}", userId, e.getMessage());
throw e;
} catch (IOException e) {
log.error("IO error storing warehouse access image for user {}: {}", userId, e.getMessage(), e);
throw new RuntimeException("Failed to store warehouse access image: " + e.getMessage(), e);
} catch (Exception e) {
log.error("Unexpected error storing warehouse access image for user {}: {}", userId, e.getMessage(), e);
throw new RuntimeException("Failed to store warehouse access image: " + e.getMessage(), e);
}
}

public Path getWarehouseAccessImagePath(String filename) {
return Paths.get(warehouseAccessImagesDir).resolve(filename);
}
}

