package com.raf.service;

import com.raf.entity.Location;
import com.raf.enums.ELocation;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationService {
private static final String LOCATION_NOT_FOUND_WITH_ID = "Location not found with id: ";
private static final String LOCATION_NOT_FOUND_WITH_CODE = "Location not found with code: ";

private final LocationRepository locationRepository;

public Location createLocation(Location location) {
log.info("Creating location: {} (type: {})", location.getName(), location.getType());



if (location.getParent() == null && location.getType() != ELocation.Province) {
log.warn("Location {} of type {} has no parent. This might be an error for non-province locations.",
location.getName(), location.getType());
}


if (locationRepository.findByCode(location.getCode()).isPresent()) {
throw new com.raf.exception.DuplicateResourceException(
"Location with code '" + location.getCode() + "' already exists");
}

Location saved = locationRepository.save(location);
log.info("Successfully created location: {} with code: {}", saved.getName(), saved.getCode());
return saved;
}

public Location getLocationById(UUID id) {
return locationRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND_WITH_ID + id));
}

public Location getLocationByCode(String code) {
return locationRepository.findByCode(code)
.orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND_WITH_CODE + code));
}

public List<Location> getAllLocations() {
return locationRepository.findAll();
}

public List<Location> getLocationsByType(ELocation type) {
return locationRepository.findByType(type);
}

public List<Location> getLocationsByType(ELocation type, Sort sort) {
return locationRepository.findByType(type, sort);
}

public Page<Location> getLocationsByType(ELocation type, Pageable pageable) {
return locationRepository.findByType(type, pageable);
}

public List<Location> getProvinces() {
return locationRepository.findByParentIsNull();
}

public List<Location> getChildLocations(UUID parentId) {
log.debug("Fetching child locations for parentId: {}", parentId);
List<Location> children = locationRepository.findByParentId(parentId);
log.debug("Found {} child locations for parentId: {}", children.size(), parentId);
if (children.isEmpty()) {
log.warn("No child locations found for parentId: {}. This might indicate missing data.", parentId);
Location parent = locationRepository.findById(parentId).orElse(null);
if (parent != null) {
log.warn("Parent location: {} ({}), type: {}", parent.getName(), parent.getCode(), parent.getType());
} else {
log.error("Parent location with ID {} does not exist!", parentId);
}
} else {
log.debug("Child locations: {}", children.stream()
.map(l -> String.format("%s (%s, type: %s)", l.getName(), l.getCode(), l.getType()))
.toList());
}
return children;
}

public Map<String, Object> getChildLocationsCount(UUID parentId) {
Location parent = locationRepository.findById(parentId).orElse(null);
List<Location> children = locationRepository.findByParentId(parentId);

Map<String, Object> result = new HashMap<>();
if (parent != null) {
Map<String, String> parentInfo = new HashMap<>();
parentInfo.put("id", parent.getId().toString());
parentInfo.put("name", parent.getName());
parentInfo.put("code", parent.getCode());
parentInfo.put("type", parent.getType().toString());
result.put("parent", parentInfo);
} else {
result.put("parent", "NOT FOUND");
}
result.put("childrenCount", children.size());
result.put("children", children.stream()
.map(l -> {
Map<String, String> childInfo = new HashMap<>();
childInfo.put("id", l.getId().toString());
childInfo.put("name", l.getName());
childInfo.put("code", l.getCode());
childInfo.put("type", l.getType().toString());
return childInfo;
})
.toList());
return result;
}

public Location updateLocation(UUID id, Location locationDetails) {
Location location = locationRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND_WITH_ID + id));
location.setCode(locationDetails.getCode());
location.setName(locationDetails.getName());
location.setType(locationDetails.getType());
if (locationDetails.getParent() != null) {
location.setParent(locationDetails.getParent());
}
return locationRepository.save(location);
}

public List<Location> searchByName(String name) {
return locationRepository.searchByName(name);
}

public void deleteLocation(UUID id) {
Location location = locationRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND_WITH_ID + id));


List<Location> children = locationRepository.findByParentId(id);
if (!children.isEmpty()) {
throw new com.raf.exception.OperationNotAllowedException(
"Cannot delete location '" + location.getName() + "' because it has " + children.size() + " child locations");
}

locationRepository.delete(location);
log.info("Deleted location: {}", location.getName());
}


@Transactional(readOnly = true)
public List<Location> getFilteredProvinces(com.raf.entity.User user, List<com.raf.entity.StorageWarehouse> warehouses) {
if (user == null) {

return getProvinces();
}

if (user.getUserType() == com.raf.enums.UserType.ADMIN) {

return getProvinces();
}

if (user.getUserType() == com.raf.enums.UserType.FARMER) {

if (user.getLocation() == null) {
log.warn("Farmer {} has no location assigned", user.getId());
return List.of();
}


Location current = user.getLocation();
int depth = 0;
while (current != null && current.getParent() != null && depth < 5) {
current = current.getParent();
depth++;
}

if (current != null && current.getType() == ELocation.Province) {
return List.of(current);
}

log.warn("Could not find province for farmer {}", user.getId());
return List.of();
}

if (user.getUserType() == com.raf.enums.UserType.STOREKEEPER) {

if (warehouses == null || warehouses.isEmpty()) {
log.warn("Storekeeper {} has no assigned warehouses", user.getId());
return List.of();
}

java.util.Set<UUID> provinceIds = new java.util.HashSet<>();
for (com.raf.entity.StorageWarehouse warehouse : warehouses) {
if (warehouse.getLocation() != null) {
Location current = warehouse.getLocation();
int depth = 0;
while (current != null && current.getParent() != null && depth < 5) {
current = current.getParent();
depth++;
}

if (current != null && current.getType() == ELocation.Province) {
provinceIds.add(current.getId());
}
}
}

if (provinceIds.isEmpty()) {
return List.of();
}

return locationRepository.findAllById(provinceIds);
}


return getProvinces();
}
}

