package com.raf.Rangira.Agro.Farming.config;

import com.raf.Rangira.Agro.Farming.entity.*;
import com.raf.Rangira.Agro.Farming.enums.*;
import com.raf.Rangira.Agro.Farming.repository.*;
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
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StorageWarehouseRepository warehouseRepository;
    private final CropTypeRepository cropTypeRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (locationRepository.count() == 0) {
            log.info("Starting Data Seeding...");
            seedLocations();
            seedCropTypes();
            seedSampleUsers();
            seedSampleWarehouses();
            log.info("Data Seeding Completed!");
        } else {
            log.info("Data already exists. Skipping seeding.");
        }
    }
    
    private void seedLocations() {
        log.info("Seeding Rwandan Locations...");
        
        Location kigali = createLocation("KIG", "Kigali City", LocationLevel.PROVINCE, null);
        Location gasabo = createLocation("GAS", "Gasabo", LocationLevel.DISTRICT, kigali);
        Location kimironko = createLocation("KIM", "Kimironko", LocationLevel.SECTOR, gasabo);
        Location kibagabaga = createLocation("KIB", "Kibagabaga", LocationLevel.CELL, kimironko);
        createLocation("KIB01", "Kibagabaga Village", LocationLevel.VILLAGE, kibagabaga);
        
        Location northern = createLocation("NOR", "Northern Province", LocationLevel.PROVINCE, null);
        Location musanze = createLocation("MUS", "Musanze", LocationLevel.DISTRICT, northern);
        Location muhoza = createLocation("MUH", "Muhoza", LocationLevel.SECTOR, musanze);
        Location gitega = createLocation("GIT", "Gitega", LocationLevel.CELL, muhoza);
        createLocation("GIT01", "Gitega Village", LocationLevel.VILLAGE, gitega);
        
        Location cyuve = createLocation("CYU", "Cyuve", LocationLevel.SECTOR, musanze);
        Location karambi = createLocation("KAR", "Karambi", LocationLevel.CELL, cyuve);
        createLocation("KAR01", "Karambi Village", LocationLevel.VILLAGE, karambi);
        
        Location southern = createLocation("SOU", "Southern Province", LocationLevel.PROVINCE, null);
        Location huye = createLocation("HUY", "Huye", LocationLevel.DISTRICT, southern);
        Location tumba = createLocation("TUM", "Tumba", LocationLevel.SECTOR, huye);
        Location cyarwa = createLocation("CYA", "Cyarwa", LocationLevel.CELL, tumba);
        createLocation("CYA01", "Cyarwa Village", LocationLevel.VILLAGE, cyarwa);
        
        Location eastern = createLocation("EAS", "Eastern Province", LocationLevel.PROVINCE, null);
        Location nyagatare = createLocation("NYG", "Nyagatare", LocationLevel.DISTRICT, eastern);
        Location matimba = createLocation("MAT", "Matimba", LocationLevel.SECTOR, nyagatare);
        Location matimba_cell = createLocation("MATC", "Matimba Cell", LocationLevel.CELL, matimba);
        createLocation("MATV", "Matimba Village", LocationLevel.VILLAGE, matimba_cell);
        
        Location western = createLocation("WES", "Western Province", LocationLevel.PROVINCE, null);
        Location rusizi = createLocation("RUS", "Rusizi", LocationLevel.DISTRICT, western);
        Location kamembe = createLocation("KAM", "Kamembe", LocationLevel.SECTOR, rusizi);
        Location kamembe_cell = createLocation("KAMC", "Kamembe Cell", LocationLevel.CELL, kamembe);
        createLocation("KAMV", "Kamembe Village", LocationLevel.VILLAGE, kamembe_cell);
        
        log.info("Locations seeded successfully");
    }
    
    private Location createLocation(String code, String name, LocationLevel level, Location parent) {
        Location location = new Location();
        location.setCode(code);
        location.setName(name);
        location.setLevel(level);
        location.setParent(parent);
        return locationRepository.save(location);
    }
    
    private void seedCropTypes() {
        log.info("Seeding Crop Types...");
        
        createCropType("CRP-MAI", "Maize", CropCategory.CEREALS, MeasurementUnit.KG);
        createCropType("CRP-BEA", "Beans", CropCategory.LEGUMES, MeasurementUnit.KG);
        createCropType("CRP-RIC", "Rice", CropCategory.CEREALS, MeasurementUnit.KG);
        createCropType("CRP-WHE", "Wheat", CropCategory.CEREALS, MeasurementUnit.KG);
        createCropType("CRP-POT", "Irish Potatoes", CropCategory.TUBERS, MeasurementUnit.KG);
        createCropType("CRP-CAS", "Cassava", CropCategory.TUBERS, MeasurementUnit.KG);
        createCropType("CRP-TOM", "Tomatoes", CropCategory.VEGETABLES, MeasurementUnit.KG);
        createCropType("CRP-CAB", "Cabbage", CropCategory.VEGETABLES, MeasurementUnit.KG);
        
        log.info("Crop Types seeded");
    }
    
    private void createCropType(String code, String name, CropCategory category, MeasurementUnit unit) {
        CropType cropType = new CropType();
        cropType.setCropCode(code);
        cropType.setCropName(name);
        cropType.setCategory(category);
        cropType.setMeasurementUnit(unit);
        cropTypeRepository.save(cropType);
    }
    
    private void seedSampleUsers() {
        log.info("Seeding Sample Users...");
        
        Location gitejaVillage = locationRepository.findByCode("GIT01")
                .orElseThrow(() -> new RuntimeException("Village not found"));
        Location karambiVillage = locationRepository.findByCode("KAR01")
                .orElseThrow(() -> new RuntimeException("Village not found"));
        
        createUser("USR-ADM-001", "Admin", "User", "admin@rangira.rw", 
                   "+250788000001", "admin123", UserType.ADMIN, gitejaVillage);
        
        createUser("USR-FAR-001", "Jean", "Uwimana", "jean.uwimana@farmer.rw",
                   "+250788000002", "farmer123", UserType.FARMER, gitejaVillage);
        
        createUser("USR-FAR-002", "Marie", "Mukamana", "marie.mukamana@farmer.rw",
                   "+250788000003", "farmer123", UserType.FARMER, karambiVillage);
        
        createUser("USR-BUY-001", "Emmanuel", "Kagame", "emmanuel.kagame@buyer.rw",
                   "+250788000004", "buyer123", UserType.BUYER, gitejaVillage);
        
        createUser("USR-STK-001", "Joseph", "Habimana", "joseph.habimana@storekeeper.rw",
                   "+250788000005", "storekeeper123", UserType.STOREKEEPER, gitejaVillage);
        
        log.info("Sample Users seeded");
    }
    
    private void createUser(String userCode, String firstName, String lastName, 
                          String email, String phone, String password, 
                          UserType userType, Location location) {
        User user = new User();
        user.setUserCode(userCode);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(password);
        user.setUserType(userType);
        user.setStatus(UserStatus.ACTIVE);
        user.setLocation(location);
        User savedUser = userRepository.save(user);
        
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setVerified(false);
        userProfileRepository.save(profile);
    }
    
    private void seedSampleWarehouses() {
        log.info("Seeding Sample Warehouses...");
        
        Location gitejaVillage = locationRepository.findByCode("GIT01")
                .orElseThrow(() -> new RuntimeException("Village not found"));
        Location karambiVillage = locationRepository.findByCode("KAR01")
                .orElseThrow(() -> new RuntimeException("Village not found"));
        
        createWarehouse("WH-MUS-001", "Musanze Cooperative Store", 
                       WarehouseType.COOPERATIVE, new BigDecimal("50000"), gitejaVillage);
        
        createWarehouse("WH-MUS-002", "Musanze Government Storage",
                       WarehouseType.GOVERNMENT, new BigDecimal("100000"), gitejaVillage);
        
        createWarehouse("WH-KAR-001", "Karambi Private Warehouse",
                       WarehouseType.PRIVATE, new BigDecimal("30000"), karambiVillage);
        
        log.info("Sample Warehouses seeded");
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
