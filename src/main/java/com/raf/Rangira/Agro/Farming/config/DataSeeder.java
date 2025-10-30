package com.raf.Rangira.Agro.Farming.config;

import com.raf.Rangira.Agro.Farming.entity.CropType;
import com.raf.Rangira.Agro.Farming.entity.Location;
import com.raf.Rangira.Agro.Farming.entity.StorageWarehouse;
import com.raf.Rangira.Agro.Farming.enums.CropCategory;
import com.raf.Rangira.Agro.Farming.enums.LocationLevel;
import com.raf.Rangira.Agro.Farming.enums.MeasurementUnit;
import com.raf.Rangira.Agro.Farming.enums.WarehouseStatus;
import com.raf.Rangira.Agro.Farming.enums.WarehouseType;
import com.raf.Rangira.Agro.Farming.repository.CropTypeRepository;
import com.raf.Rangira.Agro.Farming.repository.LocationRepository;
import com.raf.Rangira.Agro.Farming.repository.StorageWarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    
    private final LocationRepository locationRepository;
    private final CropTypeRepository cropTypeRepository;
    private final StorageWarehouseRepository warehouseRepository;
    
    @Override
    public void run(String... args) {
        seedLocationsIfEmpty();
        seedCropTypesIfEmpty();
        seedWarehousesIfEmpty();

        log.info("=================================================");
        log.info("  Rangira Agro Farming Application is READY!");
        log.info("  API Documentation: http://localhost:8080/swagger-ui.html");
        log.info("  Server running on: http://localhost:8080");
        log.info("=================================================");
    }

    private void seedLocationsIfEmpty() {
        if (locationRepository.count() > 0) return;

        Location province = Location.builder()
                .code("PRV-KGL")
                .name("Kigali Province")
                .level(LocationLevel.PROVINCE)
                .build();
        province = locationRepository.save(province);

        Location district = Location.builder()
                .code("DST-GAS")
                .name("Gasabo District")
                .level(LocationLevel.DISTRICT)
                .parent(province)
                .build();
        district = locationRepository.save(district);

        Location sector = Location.builder()
                .code("SCTR-KIM")
                .name("Kimironko Sector")
                .level(LocationLevel.SECTOR)
                .parent(district)
                .build();
        sector = locationRepository.save(sector);

        Location cell = Location.builder()
                .code("CELL-BIB")
                .name("Bibare Cell")
                .level(LocationLevel.CELL)
                .parent(sector)
                .build();
        cell = locationRepository.save(cell);

        Location village = Location.builder()
                .code("VLG-KGL001")
                .name("Village 1")
                .level(LocationLevel.VILLAGE)
                .parent(cell)
                .build();
        locationRepository.save(village);
    }

    private void seedCropTypesIfEmpty() {
        if (cropTypeRepository.count() > 0) return;

        cropTypeRepository.save(newCrop("CRP-MAI", "Maize", CropCategory.CEREALS));
        cropTypeRepository.save(newCrop("CRP-BEA", "Beans", CropCategory.LEGUMES));
        cropTypeRepository.save(newCrop("CRP-RIC", "Rice", CropCategory.CEREALS));
        cropTypeRepository.save(newCrop("CRP-POT", "Potatoes", CropCategory.TUBERS));
    }

    private CropType newCrop(String code, String name, CropCategory category) {
        CropType ct = new CropType();
        ct.setCropCode(code);
        ct.setCropName(name);
        ct.setCategory(category);
        ct.setMeasurementUnit(MeasurementUnit.KG);
        ct.setDescription(null);
        return ct;
    }

    private void seedWarehousesIfEmpty() {
        if (warehouseRepository.count() > 0) return;

        Location anySector = locationRepository.findByCode("SCTR-KIM")
                .orElseGet(() -> locationRepository.findAll().stream()
                        .filter(l -> l.getLevel() == LocationLevel.SECTOR)
                        .findFirst()
                        .orElse(null));
        if (anySector == null) return;

        StorageWarehouse w = new StorageWarehouse();
        w.setWarehouseCode("WH-KIM-001");
        w.setWarehouseName("Kimironko Central Warehouse");
        w.setWarehouseType(WarehouseType.COOPERATIVE);
        w.setTotalCapacityKg(new BigDecimal("50000"));
        w.setAvailableCapacityKg(new BigDecimal("50000"));
        w.setStatus(WarehouseStatus.ACTIVE);
        w.setLocation(anySector);
        warehouseRepository.save(w);
    }
}


