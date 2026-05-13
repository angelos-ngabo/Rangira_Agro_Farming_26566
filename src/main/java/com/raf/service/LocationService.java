package com.raf.service;

import com.raf.constant.RwandaLocationDefaults;
import com.raf.entity.Location;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LocationService {

private final LocationRepository locationRepository;

public Location getLocationById(UUID id) {
return locationRepository.findById(id)
.orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
}

public Location getLocationByCode(String code) {
return locationRepository.findByCode(code)
.orElseThrow(() -> new ResourceNotFoundException("Location not found with code: " + code));
}

public List<String> getProvinces() {
List<String> fromDb = locationRepository.findDistinctProvinces();
if (!fromDb.isEmpty()) {
return fromDb;
}
log.warn(
"No rows in location table (seeds.sql likely did not run or failed). "
+ "Returning default Rwanda province names; districts/villages stay empty until seeding succeeds.");
return RwandaLocationDefaults.PROVINCE_NAMES;
}

public List<String> getDistricts(String province) {
return locationRepository.findDistinctDistrictsByProvince(province);
}

public List<String> getSectors(String province, String district) {
return locationRepository.findDistinctSectorsByProvinceAndDistrict(province, district);
}

public List<String> getCells(String province, String district, String sector) {
return locationRepository.findDistinctCellsByProvinceAndDistrictAndSector(province, district, sector);
}

public List<Location> getVillages(String province, String district, String sector, String cell) {
return locationRepository.findVillagesByProvinceAndDistrictAndSectorAndCell(province, district, sector, cell);
}

public List<Location> searchByName(String name) {
return locationRepository.searchByVillageName(name);
}
}

