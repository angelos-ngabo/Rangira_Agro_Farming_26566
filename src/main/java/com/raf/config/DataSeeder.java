package com.raf.config;

import com.raf.entity.CropType;
import com.raf.entity.Location;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.User;
import com.raf.entity.UserProfile;
import com.raf.enums.CropCategory;
import com.raf.enums.LocationLevel;
import com.raf.enums.MeasurementUnit;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import com.raf.enums.WarehouseStatus;
import com.raf.enums.WarehouseType;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.LocationRepository;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.UserProfileRepository;
import com.raf.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    
    @Override
    public void run(String... args) {
        seedLocationsIfEmpty();
        seedCropTypesIfEmpty();
        seedUsersIfEmpty();
        seedWarehousesIfEmpty();

        log.info("=================================================");
        log.info("  Rangira Agro Farming Application is READY!");
        log.info("  API Documentation: http://localhost:8080/swagger-ui.html");
        log.info("  Server running on: http://localhost:8080");
        log.info("=================================================");
    }

    private void seedLocationsIfEmpty() {
        Location kigali = locationRepository.findByCode("PRV-KGL")
                .orElseGet(() -> createProvince("PRV-KGL", "Kigali Province"));
        Location northern = locationRepository.findByCode("PRV-NORTH")
                .orElseGet(() -> createProvince("PRV-NORTH", "Northern Province"));
        Location southern = locationRepository.findByCode("PRV-SOUTH")
                .orElseGet(() -> createProvince("PRV-SOUTH", "Southern Province"));
        Location eastern = locationRepository.findByCode("PRV-EAST")
                .orElseGet(() -> createProvince("PRV-EAST", "Eastern Province"));
        Location western = locationRepository.findByCode("PRV-WEST")
                .orElseGet(() -> createProvince("PRV-WEST", "Western Province"));

        Location gasabo = locationRepository.findByCode("DST-GAS")
                .orElseGet(() -> createDistrict("DST-GAS", "Gasabo District", kigali));
        Location nyarugenge = locationRepository.findByCode("DST-NYA")
                .orElseGet(() -> createDistrict("DST-NYA", "Nyarugenge District", kigali));
        Location kicukiro = locationRepository.findByCode("DST-KIC")
                .orElseGet(() -> createDistrict("DST-KIC", "Kicukiro District", kigali));

        Location musanze = locationRepository.findByCode("DST-MUS")
                .orElseGet(() -> createDistrict("DST-MUS", "Musanze District", northern));
        Location burera = locationRepository.findByCode("DST-BUR")
                .orElseGet(() -> createDistrict("DST-BUR", "Burera District", northern));
        Location gicumbi = locationRepository.findByCode("DST-GIC")
                .orElseGet(() -> createDistrict("DST-GIC", "Gicumbi District", northern));

        Location huye = locationRepository.findByCode("DST-HUY")
                .orElseGet(() -> createDistrict("DST-HUY", "Huye District", southern));
        Location nyamagabe = locationRepository.findByCode("DST-NYM")
                .orElseGet(() -> createDistrict("DST-NYM", "Nyamagabe District", southern));
        Location nyanza = locationRepository.findByCode("DST-NYZ")
                .orElseGet(() -> createDistrict("DST-NYZ", "Nyanza District", southern));

        Location rwamagana = locationRepository.findByCode("DST-RWA")
                .orElseGet(() -> createDistrict("DST-RWA", "Rwamagana District", eastern));
        Location kayonza = locationRepository.findByCode("DST-KAY")
                .orElseGet(() -> createDistrict("DST-KAY", "Kayonza District", eastern));
        Location ngoma = locationRepository.findByCode("DST-NGO")
                .orElseGet(() -> createDistrict("DST-NGO", "Ngoma District", eastern));

        Location rubavu = locationRepository.findByCode("DST-RUB")
                .orElseGet(() -> createDistrict("DST-RUB", "Rubavu District", western));
        Location karongi = locationRepository.findByCode("DST-KAR")
                .orElseGet(() -> createDistrict("DST-KAR", "Karongi District", western));
        Location rutsiro = locationRepository.findByCode("DST-RUT")
                .orElseGet(() -> createDistrict("DST-RUT", "Rutsiro District", western));

        Location kimironko = locationRepository.findByCode("SCTR-KIM")
                .orElseGet(() -> createSector("SCTR-KIM", "Kimironko Sector", gasabo));
        Location remera = locationRepository.findByCode("SCTR-REM")
                .orElseGet(() -> createSector("SCTR-REM", "Remera Sector", gasabo));
        Location gisozi = locationRepository.findByCode("SCTR-GIS")
                .orElseGet(() -> createSector("SCTR-GIS", "Gisozi Sector", gasabo));

        Location kacyiru = locationRepository.findByCode("SCTR-KAC")
                .orElseGet(() -> createSector("SCTR-KAC", "Kacyiru Sector", nyarugenge));
        Location nyarugengeSector = locationRepository.findByCode("SCTR-NYA")
                .orElseGet(() -> createSector("SCTR-NYA", "Nyarugenge Sector", nyarugenge));

        Location kanombe = locationRepository.findByCode("SCTR-KAN")
                .orElseGet(() -> createSector("SCTR-KAN", "Kanombe Sector", kicukiro));
        Location gikondo = locationRepository.findByCode("SCTR-GIK")
                .orElseGet(() -> createSector("SCTR-GIK", "Gikondo Sector", kicukiro));

        Location kinigi = locationRepository.findByCode("SCTR-KIN")
                .orElseGet(() -> createSector("SCTR-KIN", "Kinigi Sector", musanze));
        Location muhoza = locationRepository.findByCode("SCTR-MUH")
                .orElseGet(() -> createSector("SCTR-MUH", "Muhoza Sector", musanze));

        Location rukara = locationRepository.findByCode("SCTR-RUK")
                .orElseGet(() -> createSector("SCTR-RUK", "Rukara Sector", kayonza));
        Location mwiri = locationRepository.findByCode("SCTR-MWI")
                .orElseGet(() -> createSector("SCTR-MWI", "Mwiri Sector", kayonza));

        Location bibare = locationRepository.findByCode("CELL-BIB")
                .orElseGet(() -> createCell("CELL-BIB", "Bibare Cell", kimironko));
        Location gacuriro = locationRepository.findByCode("CELL-GAC")
                .orElseGet(() -> createCell("CELL-GAC", "Gacuriro Cell", kimironko));
        Location kinyinya = locationRepository.findByCode("CELL-KIN")
                .orElseGet(() -> createCell("CELL-KIN", "Kinyinya Cell", remera));

        Location kagugu = locationRepository.findByCode("CELL-KAG")
                .orElseGet(() -> createCell("CELL-KAG", "Kagugu Cell", kacyiru));
        Location rwezamenyo = locationRepository.findByCode("CELL-RWE")
                .orElseGet(() -> createCell("CELL-RWE", "Rwezamenyo Cell", nyarugengeSector));

        Location gahanga = locationRepository.findByCode("CELL-GAH")
                .orElseGet(() -> createCell("CELL-GAH", "Gahanga Cell", kanombe));
        Location kagarama = locationRepository.findByCode("CELL-KAG2")
                .orElseGet(() -> createCell("CELL-KAG2", "Kagarama Cell", gikondo));

        Location cyuve = locationRepository.findByCode("CELL-CYU")
                .orElseGet(() -> createCell("CELL-CYU", "Cyuve Cell", kinigi));
        Location nyange = locationRepository.findByCode("CELL-NYA")
                .orElseGet(() -> createCell("CELL-NYA", "Nyange Cell", muhoza));

        Location rukaraCell = locationRepository.findByCode("CELL-RUK2")
                .orElseGet(() -> createCell("CELL-RUK2", "Rukara Cell", rukara));
        Location gahini = locationRepository.findByCode("CELL-GAH2")
                .orElseGet(() -> createCell("CELL-GAH2", "Gahini Cell", mwiri));

        Location village1 = locationRepository.findByCode("VLG-KGL001")
                .orElseGet(() -> createVillage("VLG-KGL001", "Kigali Village 1", bibare));
        Location village2 = locationRepository.findByCode("VLG-KGL002")
                .orElseGet(() -> createVillage("VLG-KGL002", "Kigali Village 2", gacuriro));
        Location village3 = locationRepository.findByCode("VLG-KGL003")
                .orElseGet(() -> createVillage("VLG-KGL003", "Kigali Village 3", kinyinya));
        Location village4 = locationRepository.findByCode("VLG-KGL004")
                .orElseGet(() -> createVillage("VLG-KGL004", "Kigali Village 4", kagugu));
        Location village5 = locationRepository.findByCode("VLG-KGL005")
                .orElseGet(() -> createVillage("VLG-KGL005", "Kigali Village 5", rwezamenyo));
        Location village6 = locationRepository.findByCode("VLG-KGL006")
                .orElseGet(() -> createVillage("VLG-KGL006", "Kigali Village 6", gahanga));
        Location village7 = locationRepository.findByCode("VLG-KGL007")
                .orElseGet(() -> createVillage("VLG-KGL007", "Kigali Village 7", kagarama));

        Location village8 = locationRepository.findByCode("VLG-NORTH001")
                .orElseGet(() -> createVillage("VLG-NORTH001", "Northern Village 1", cyuve));
        Location village9 = locationRepository.findByCode("VLG-NORTH002")
                .orElseGet(() -> createVillage("VLG-NORTH002", "Northern Village 2", nyange));

        Location village10 = locationRepository.findByCode("VLG-EAST001")
                .orElseGet(() -> createVillage("VLG-EAST001", "Eastern Village 1", rukaraCell));
        Location village11 = locationRepository.findByCode("VLG-EAST002")
                .orElseGet(() -> createVillage("VLG-EAST002", "Eastern Village 2", gahini));

        log.info("Seeded all locations: 5 Provinces, 15 Districts, 12 Sectors, 10 Cells, 11 Villages");
    }

    private Location createProvince(String code, String name) {
        Location province = Location.builder()
                .code(code)
                .name(name)
                .level(LocationLevel.PROVINCE)
                .build();
        return locationRepository.save(province);
    }

    private Location createDistrict(String code, String name, Location province) {
        Location district = Location.builder()
                .code(code)
                .name(name)
                .level(LocationLevel.DISTRICT)
                .parent(province)
                .build();
        return locationRepository.save(district);
    }

    private Location createSector(String code, String name, Location district) {
        Location sector = Location.builder()
                .code(code)
                .name(name)
                .level(LocationLevel.SECTOR)
                .parent(district)
                .build();
        return locationRepository.save(sector);
    }

    private Location createCell(String code, String name, Location sector) {
        Location cell = Location.builder()
                .code(code)
                .name(name)
                .level(LocationLevel.CELL)
                .parent(sector)
                .build();
        return locationRepository.save(cell);
    }

    private Location createVillage(String code, String name, Location cell) {
        Location village = Location.builder()
                .code(code)
                .name(name)
                .level(LocationLevel.VILLAGE)
                .parent(cell)
                .build();
        return locationRepository.save(village);
    }

    private void seedCropTypesIfEmpty() {
        if (cropTypeRepository.count() > 0) return;

        cropTypeRepository.save(newCrop("CRP-MAI", "Maize", CropCategory.CEREALS));
        cropTypeRepository.save(newCrop("CRP-BEA", "Beans", CropCategory.LEGUMES));
        cropTypeRepository.save(newCrop("CRP-RIC", "Rice", CropCategory.CEREALS));
        cropTypeRepository.save(newCrop("CRP-POT", "Potatoes", CropCategory.TUBERS));
        cropTypeRepository.save(newCrop("CRP-WHE", "Wheat", CropCategory.CEREALS));
        cropTypeRepository.save(newCrop("CRP-CAS", "Cassava", CropCategory.TUBERS));
        cropTypeRepository.save(newCrop("CRP-TOM", "Tomatoes", CropCategory.VEGETABLES));
        cropTypeRepository.save(newCrop("CRP-CAB", "Cabbage", CropCategory.VEGETABLES));
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

    private void seedUsersIfEmpty() {
        if (userRepository.count() > 0) return;

        Location villageKigali = locationRepository.findByCode("VLG-KGL001")
                .orElseGet(() -> locationRepository.findAll().stream()
                        .filter(l -> l.getLevel() == LocationLevel.VILLAGE)
                        .findFirst()
                        .orElse(null));

        Location villageNorth = locationRepository.findByCode("VLG-NORTH001")
                .orElse(villageKigali);

        Location villageEast = locationRepository.findByCode("VLG-EAST001")
                .orElse(villageKigali);
        
        if (villageKigali == null) {
            log.warn("No village location found. Cannot seed users.");
            return;
        }

        User admin = createUser("ADM-001", "Admin", "User", "admin@rangira.rw", 
                "+250788000001", "admin123", UserType.ADMIN, UserStatus.ACTIVE, villageKigali, true);

        User farmer1 = createUser("FARM-001", "Jean", "Mukamana", "jean.farmer@rangira.rw", 
                "+250788000002", "farmer123", UserType.FARMER, UserStatus.ACTIVE, villageKigali, false);

        User farmer2 = createUser("FARM-002", "Marie", "Uwimana", "marie.farmer@rangira.rw", 
                "+250788000003", "farmer123", UserType.FARMER, UserStatus.ACTIVE, villageKigali, false);

        User farmer3 = createUser("FARM-003", "Pierre", "Nkurunziza", "pierre.farmer@rangira.rw", 
                "+250788000006", "farmer123", UserType.FARMER, UserStatus.ACTIVE, villageNorth, false);

        User farmer4 = createUser("FARM-004", "Claire", "Mukamana", "claire.farmer@rangira.rw", 
                "+250788000007", "farmer123", UserType.FARMER, UserStatus.ACTIVE, villageEast, false);

        User buyer1 = createUser("BUY-001", "Paul", "Nkurunziza", "paul.buyer@rangira.rw", 
                "+250788000004", "buyer123", UserType.BUYER, UserStatus.ACTIVE, villageKigali, false);

        User buyer2 = createUser("BUY-002", "Sarah", "Uwimana", "sarah.buyer@rangira.rw", 
                "+250788000008", "buyer123", UserType.BUYER, UserStatus.ACTIVE, villageNorth, false);

        User storekeeper1 = createUser("STORE-001", "Alice", "Mukamana", "alice.storekeeper@rangira.rw", 
                "+250788000005", "storekeeper123", UserType.STOREKEEPER, UserStatus.ACTIVE, villageKigali, false);

        User storekeeper2 = createUser("STORE-002", "David", "Nkurunziza", "david.storekeeper@rangira.rw", 
                "+250788000009", "storekeeper123", UserType.STOREKEEPER, UserStatus.ACTIVE, villageEast, false);

        log.info("Seeded 9 users: 1 Admin, 4 Farmers, 2 Buyers, 2 Storekeepers across multiple provinces");
    }

    private User createUser(String userCode, String firstName, String lastName, String email, 
                           String phoneNumber, String password, UserType userType, UserStatus status, 
                           Location location, boolean verified) {
        User user = new User();
        user.setUserCode(userCode);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        user.setUserType(userType);
        user.setStatus(status);
        user.setLocation(location);
        user = userRepository.save(user);
        
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setVerified(verified);
        userProfileRepository.save(profile);
        
        return user;
    }

    private void seedWarehousesIfEmpty() {
        if (warehouseRepository.count() > 0) return;

        Location sectorKigali = locationRepository.findByCode("SCTR-KIM")
                .orElseGet(() -> locationRepository.findAll().stream()
                        .filter(l -> l.getLevel() == LocationLevel.SECTOR)
                        .findFirst()
                        .orElse(null));

        Location sectorNorth = locationRepository.findByCode("SCTR-KIN")
                .orElse(sectorKigali);

        Location sectorEast = locationRepository.findByCode("SCTR-RUK")
                .orElse(sectorKigali);

        if (sectorKigali == null) return;

        createWarehouse("WH-KIM-001", "Kimironko Central Warehouse", 
                WarehouseType.COOPERATIVE, new BigDecimal("50000"), sectorKigali);

        createWarehouse("WH-REM-001", "Remera Storage Warehouse", 
                WarehouseType.PRIVATE, new BigDecimal("30000"), 
                locationRepository.findByCode("SCTR-REM").orElse(sectorKigali));

        createWarehouse("WH-KIN-001", "Kinigi Agricultural Warehouse", 
                WarehouseType.GOVERNMENT, new BigDecimal("75000"), sectorNorth);

        createWarehouse("WH-RUK-001", "Rukara Cooperative Warehouse", 
                WarehouseType.COOPERATIVE, new BigDecimal("40000"), sectorEast);

        log.info("Seeded 4 warehouses across different provinces");
    }

    private void createWarehouse(String code, String name, WarehouseType type, 
                                BigDecimal capacity, Location location) {
        StorageWarehouse warehouse = new StorageWarehouse();
        warehouse.setWarehouseCode(code);
        warehouse.setWarehouseName(name);
        warehouse.setWarehouseType(type);
        warehouse.setTotalCapacityKg(capacity);
        warehouse.setAvailableCapacityKg(capacity);
        warehouse.setStatus(WarehouseStatus.ACTIVE);
        warehouse.setLocation(location);
        warehouseRepository.save(warehouse);
    }
}


