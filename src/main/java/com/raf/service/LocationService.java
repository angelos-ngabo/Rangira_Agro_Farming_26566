package com.raf.service;

import com.raf.entity.Location;
import com.raf.enums.LocationLevel;
import com.raf.exception.OperationNotAllowedException;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationService {
    private static final String LOCATION_NOT_FOUND_WITH_ID = "Location not found with id: ";
    private static final String LOCATION_NOT_FOUND_WITH_CODE = "Location not found with code: ";
    
    private final LocationRepository locationRepository;

    public Location createLocation(Location location) {
        log.info("Creating location: {}", location.getName());
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
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

    public List<Location> getLocationsByLevel(LocationLevel level) {
        return locationRepository.findByLevel(level);
    }
    
    public List<Location> getLocationsByLevel(LocationLevel level, Sort sort) {
        return locationRepository.findByLevel(level, sort);
    }
    
    public Page<Location> getLocationsByLevel(LocationLevel level, Pageable pageable) {
        return locationRepository.findByLevel(level, pageable);
    }
    
    public List<Location> getProvinces() {
        return locationRepository.findByParentIsNull();
    }
    
    public List<Location> getChildLocations(Long parentId) {
        return locationRepository.findByParentId(parentId);
    }
    
    public Location updateLocation(Long id, Location locationDetails) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND_WITH_ID + id));
        location.setCode(locationDetails.getCode());
        location.setName(locationDetails.getName());
        location.setLevel(locationDetails.getLevel());
        if (locationDetails.getParent() != null) {
            location.setParent(locationDetails.getParent());
        }
        return locationRepository.save(location);
    }
    
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND_WITH_ID + id));

        int childCount = location.getChildren() == null ? 0 : location.getChildren().size();
        int userCount = location.getUsers() == null ? 0 : location.getUsers().size();
        int warehouseCount = location.getWarehouses() == null ? 0 : location.getWarehouses().size();

        if (childCount > 0 || userCount > 0 || warehouseCount > 0) {
            StringBuilder reason = new StringBuilder("Cannot delete location '")
                    .append(location.getName())
                    .append("' because it is referenced by: ");
            boolean first = true;
            if (childCount > 0) {
                reason.append(childCount).append(" child locations");
                first = false;
            }
            if (userCount > 0) {
                if (!first) reason.append(", ");
                reason.append(userCount).append(" users");
                first = false;
            }
            if (warehouseCount > 0) {
                if (!first) reason.append(", ");
                reason.append(warehouseCount).append(" warehouses");
            }

            throw new OperationNotAllowedException(reason.toString());
        }

        locationRepository.delete(location);
        log.info("Deleted location: {}", location.getName());
    }
}

