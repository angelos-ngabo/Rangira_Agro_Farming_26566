package com.raf.Rangira.Agro.Farming.config;

import com.raf.Rangira.Agro.Farming.entity.*;
import com.raf.Rangira.Agro.Farming.enums.*;
import com.raf.Rangira.Agro.Farming.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Seeder
 * Seeds initial data including Rwandan Location Hierarchy and sample data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final SectorRepository sectorRepository;
    private final CellRepository cellRepository;
    private final VillageRepository villageRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StorageWarehouseRepository warehouseRepository;
    private final CropTypeRepository cropTypeRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() == 0) {
            log.info("===========================================");
            log.info("Starting Data Seeding for Rangira Agro Farming");
            log.info("===========================================");
            
            seedRwandanLocationHierarchy();
            seedCropTypes();
            seedSampleUsers();
            seedSampleWarehouses();
            
            log.info("===========================================");
            log.info("Data Seeding Completed Successfully!");
            log.info("===========================================");
        } else {
            log.info("Data already exists. Skipping seeding.");
        }
    }
    
    private void seedRwandanLocationHierarchy() {
        log.info("Seeding Rwandan Location Hierarchy...");
        
        // ============================================
        // PROVINCE 1: KIGALI CITY
        // ============================================
        Province kigali = new Province();
        kigali.setProvinceCode("KIG");
        kigali.setProvinceName("Kigali City");
        kigali = provinceRepository.save(kigali);
        log.info("Created Province: {}", kigali.getProvinceName());
        
        // Districts in Kigali
        District gasabo = createDistrict("GAS", "Gasabo", kigali);
        District kicukiro = createDistrict("KIC", "Kicukiro", kigali);
        District nyarugenge = createDistrict("NYA", "Nyarugenge", kigali);
        
        // Sample Sectors, Cells, Villages for Gasabo
        Sector kimironko = createSector("KIM", "Kimironko", gasabo);
        Cell kibagabaga = createCell("KIB", "Kibagabaga", kimironko);
        createVillage("KIB01", "Kibagabaga 1", kibagabaga);
        createVillage("KIB02", "Kibagabaga 2", kibagabaga);
        
        // ============================================
        // PROVINCE 2: NORTHERN PROVINCE
        // ============================================
        Province northern = new Province();
        northern.setProvinceCode("NOR");
        northern.setProvinceName("Northern Province");
        northern = provinceRepository.save(northern);
        log.info("Created Province: {}", northern.getProvinceName());
        
        // Districts in Northern Province
        District musanze = createDistrict("MUS", "Musanze", northern);
        District burera = createDistrict("BUR", "Burera", northern);
        District gicumbi = createDistrict("GIC", "Gicumbi", northern);
        
        // Sample Sectors, Cells, Villages for Musanze (important for beans/maize pilot)
        Sector muhoza = createSector("MUH", "Muhoza", musanze);
        Cell gitega = createCell("GIT", "Gitega", muhoza);
        createVillage("GIT01", "Gitega 1", gitega);
        createVillage("GIT02", "Gitega 2", gitega);
        
        Sector cyuve = createSector("CYU", "Cyuve", musanze);
        Cell karambi = createCell("KAR", "Karambi", cyuve);
        createVillage("KAR01", "Karambi 1", karambi);
        createVillage("KAR02", "Karambi 2", karambi);
        
        // ============================================
        // PROVINCE 3: SOUTHERN PROVINCE
        // ============================================
        Province southern = new Province();
        southern.setProvinceCode("SOU");
        southern.setProvinceName("Southern Province");
        southern = provinceRepository.save(southern);
        log.info("Created Province: {}", southern.getProvinceName());
        
        // Districts in Southern Province
        District huye = createDistrict("HUY", "Huye", southern);
        District muhanga = createDistrict("MUH", "Muhanga", southern);
        District nyanza = createDistrict("NYZ", "Nyanza", southern);
        
        // Sample Sectors, Cells, Villages for Huye
        Sector tumba = createSector("TUM", "Tumba", huye);
        Cell gashororo = createCell("GAS", "Gashororo", tumba);
        createVillage("GAS01", "Gashororo 1", gashororo);
        createVillage("GAS02", "Gashororo 2", gashororo);
        
        // ============================================
        // PROVINCE 4: EASTERN PROVINCE
        // ============================================
        Province eastern = new Province();
        eastern.setProvinceCode("EAS");
        eastern.setProvinceName("Eastern Province");
        eastern = provinceRepository.save(eastern);
        log.info("Created Province: {}", eastern.getProvinceName());
        
        // Districts in Eastern Province
        District nyagatare = createDistrict("NYG", "Nyagatare", eastern);
        District kirehe = createDistrict("KIR", "Kirehe", eastern);
        District rwamagana = createDistrict("RWA", "Rwamagana", eastern);
        
        // Sample Sectors, Cells, Villages for Nyagatare (important for beans/maize)
        Sector matimba = createSector("MAT", "Matimba", nyagatare);
        Cell karangazi = createCell("KRZ", "Karangazi", matimba);
        createVillage("KRZ01", "Karangazi 1", karangazi);
        createVillage("KRZ02", "Karangazi 2", karangazi);
        
        // ============================================
        // PROVINCE 5: WESTERN PROVINCE
        // ============================================
        Province western = new Province();
        western.setProvinceCode("WES");
        western.setProvinceName("Western Province");
        western = provinceRepository.save(western);
        log.info("Created Province: {}", western.getProvinceName());
        
        // Districts in Western Province
        District karongi = createDistrict("KAR", "Karongi", western);
        District rusizi = createDistrict("RUS", "Rusizi", western);
        District rubavu = createDistrict("RUB", "Rubavu", western);
        
        // Sample Sectors, Cells, Villages for Karongi
        Sector bwishyura = createSector("BWI", "Bwishyura", karongi);
        Cell murambi = createCell("MUR", "Murambi", bwishyura);
        createVillage("MUR01", "Murambi 1", murambi);
        createVillage("MUR02", "Murambi 2", murambi);
        
        log.info("Rwandan Location Hierarchy seeded successfully!");
        log.info("Total Provinces: {}", provinceRepository.count());
        log.info("Total Districts: {}", districtRepository.count());
        log.info("Total Sectors: {}", sectorRepository.count());
        log.info("Total Cells: {}", cellRepository.count());
        log.info("Total Villages: {}", villageRepository.count());
    }
    
    private District createDistrict(String code, String name, Province province) {
        District district = new District();
        district.setDistrictCode(code);
        district.setDistrictName(name);
        district.setProvince(province);
        return districtRepository.save(district);
    }
    
    private Sector createSector(String code, String name, District district) {
        Sector sector = new Sector();
        sector.setSectorCode(code);
        sector.setSectorName(name);
        sector.setDistrict(district);
        return sectorRepository.save(sector);
    }
    
    private Cell createCell(String code, String name, Sector sector) {
        Cell cell = new Cell();
        cell.setCellCode(code);
        cell.setCellName(name);
        cell.setSector(sector);
        return cellRepository.save(cell);
    }
    
    private Village createVillage(String code, String name, Cell cell) {
        Village village = new Village();
        village.setVillageCode(code);
        village.setVillageName(name);
        village.setCell(cell);
        return villageRepository.save(village);
    }
    
    private void seedCropTypes() {
        log.info("Seeding Crop Types...");
        
        // CEREALS
        createCropType("MAIZ-001", "Maize", CropCategory.CEREALS, MeasurementUnit.KG, 
                "Common cereal crop, staple food in Rwanda");
        createCropType("RICE-001", "Rice", CropCategory.CEREALS, MeasurementUnit.KG, 
                "Rice production in Rwanda");
        createCropType("WHEAT-001", "Wheat", CropCategory.CEREALS, MeasurementUnit.KG, 
                "Wheat for bread and flour");
        
        // LEGUMES
        createCropType("BEAN-001", "Beans", CropCategory.LEGUMES, MeasurementUnit.KG, 
                "Common beans - major crop in Rwanda");
        createCropType("PEAS-001", "Peas", CropCategory.LEGUMES, MeasurementUnit.KG, 
                "Garden peas");
        createCropType("SOYB-001", "Soybeans", CropCategory.LEGUMES, MeasurementUnit.KG, 
                "Protein-rich legume");
        
        // TUBERS
        createCropType("POTA-001", "Irish Potatoes", CropCategory.TUBERS, MeasurementUnit.KG, 
                "Irish potatoes - major crop");
        createCropType("CASS-001", "Cassava", CropCategory.TUBERS, MeasurementUnit.KG, 
                "Cassava root crop");
        createCropType("SWPO-001", "Sweet Potatoes", CropCategory.TUBERS, MeasurementUnit.KG, 
                "Orange-fleshed sweet potatoes");
        
        // VEGETABLES
        createCropType("TOMA-001", "Tomatoes", CropCategory.VEGETABLES, MeasurementUnit.KG, 
                "Fresh tomatoes");
        createCropType("CABB-001", "Cabbage", CropCategory.VEGETABLES, MeasurementUnit.KG, 
                "Green cabbage");
        
        log.info("Crop Types seeded: {}", cropTypeRepository.count());
    }
    
    private void createCropType(String code, String name, CropCategory category, 
                                MeasurementUnit unit, String description) {
        CropType cropType = new CropType();
        cropType.setCropCode(code);
        cropType.setCropName(name);
        cropType.setCategory(category);
        cropType.setMeasurementUnit(unit);
        cropType.setDescription(description);
        cropTypeRepository.save(cropType);
    }
    
    private void seedSampleUsers() {
        log.info("Seeding Sample Users...");
        
        // Get sample villages
        Village gitega1 = villageRepository.findByVillageCode("GIT01").orElse(null);
        Village karambi1 = villageRepository.findByVillageCode("KAR01").orElse(null);
        Village kibagabaga1 = villageRepository.findByVillageCode("KIB01").orElse(null);
        
        if (gitega1 == null || karambi1 == null) {
            log.warn("Villages not found for sample users");
            return;
        }
        
        // Admin User
        createUser("USR-ADM-001", "Admin", "User", "admin@rangira.rw", 
                "+250788000001", "admin123", UserType.ADMIN, kibagabaga1);
        
        // Farmers
        createUser("USR-FAR-001", "Jean", "Uwimana", "jean.uwimana@farmer.rw", 
                "+250788111001", "farmer123", UserType.FARMER, gitega1);
        createUser("USR-FAR-002", "Marie", "Mukamana", "marie.mukamana@farmer.rw", 
                "+250788111002", "farmer123", UserType.FARMER, gitega1);
        createUser("USR-FAR-003", "Patrick", "Niyonzima", "patrick.niyonzima@farmer.rw", 
                "+250788111003", "farmer123", UserType.FARMER, karambi1);
        
        // Storekeepers
        createUser("USR-STK-001", "Joseph", "Habimana", "joseph.habimana@storekeeper.rw", 
                "+250788222001", "storekeeper123", UserType.STOREKEEPER, gitega1);
        createUser("USR-STK-002", "Alice", "Uwase", "alice.uwase@storekeeper.rw", 
                "+250788222002", "storekeeper123", UserType.STOREKEEPER, karambi1);
        
        // Buyers
        createUser("USR-BUY-001", "Emmanuel", "Kagame", "emmanuel.kagame@buyer.rw", 
                "+250788333001", "buyer123", UserType.BUYER, kibagabaga1);
        createUser("USR-BUY-002", "Grace", "Mutoni", "grace.mutoni@buyer.rw", 
                "+250788333002", "buyer123", UserType.BUYER, kibagabaga1);
        
        log.info("Sample Users seeded: {}", userRepository.count());
    }
    
    private void createUser(String userCode, String firstName, String lastName, 
                           String email, String phone, String password, 
                           UserType userType, Village village) {
        User user = new User();
        user.setUserCode(userCode);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserType(userType);
        user.setStatus(UserStatus.ACTIVE);
        user.setVillage(village);
        user = userRepository.save(user);
        
        // Create user profile
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setVerified(true);
        profile.setAverageRating(BigDecimal.valueOf(4.5));
        userProfileRepository.save(profile);
    }
    
    private void seedSampleWarehouses() {
        log.info("Seeding Sample Warehouses...");
        
        // Get sample villages
        Village gitega1 = villageRepository.findByVillageCode("GIT01").orElse(null);
        Village karambi1 = villageRepository.findByVillageCode("KAR01").orElse(null);
        
        if (gitega1 == null || karambi1 == null) {
            log.warn("Villages not found for sample warehouses");
            return;
        }
        
        // Warehouse in Musanze (Gitega)
        createWarehouse("WH-MUS-001", "Musanze Cooperative Store", 
                WarehouseType.COOPERATIVE, BigDecimal.valueOf(50000), gitega1);
        
        createWarehouse("WH-MUS-002", "Musanze Government Storage", 
                WarehouseType.GOVERNMENT, BigDecimal.valueOf(100000), gitega1);
        
        // Warehouse in Karambi
        createWarehouse("WH-KAR-001", "Karambi Private Warehouse", 
                WarehouseType.PRIVATE, BigDecimal.valueOf(30000), karambi1);
        
        log.info("Sample Warehouses seeded: {}", warehouseRepository.count());
    }
    
    private void createWarehouse(String code, String name, WarehouseType type, 
                                BigDecimal capacity, Village village) {
        StorageWarehouse warehouse = new StorageWarehouse();
        warehouse.setWarehouseCode(code);
        warehouse.setWarehouseName(name);
        warehouse.setWarehouseType(type);
        warehouse.setTotalCapacityKg(capacity);
        warehouse.setAvailableCapacityKg(capacity); // Initially all available
        warehouse.setStatus(WarehouseStatus.ACTIVE);
        warehouse.setVillage(village);
        warehouseRepository.save(warehouse);
    }
}

