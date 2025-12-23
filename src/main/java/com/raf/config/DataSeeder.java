package com.raf.config;

import com.raf.entity.CropType;
import com.raf.entity.Inventory;
import com.raf.entity.Location;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.entity.UserProfile;
import com.raf.entity.WarehouseAccess;
import com.raf.enums.AccessLevel;
import com.raf.enums.CropCategory;
import com.raf.enums.DeliveryStatus;
import com.raf.enums.ELocation;
import com.raf.enums.InventoryStatus;
import com.raf.enums.MeasurementUnit;
import com.raf.enums.PaymentStatus;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import com.raf.enums.WarehouseAccessStatus;
import com.raf.enums.WarehouseStatus;
import com.raf.enums.WarehouseType;
import com.raf.repository.CropTypeRepository;
import com.raf.repository.InventoryRepository;
import com.raf.repository.LocationRepository;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserProfileRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

private final LocationRepository locationRepository;
private final CropTypeRepository cropTypeRepository;
private final StorageWarehouseRepository warehouseRepository;
private final UserRepository userRepository;
private final UserProfileRepository userProfileRepository;
private final WarehouseAccessRepository warehouseAccessRepository;
private final InventoryRepository inventoryRepository;
private final TransactionRepository transactionRepository;
private final PasswordEncoder passwordEncoder;

@PersistenceContext
private EntityManager entityManager;

@Override
public void run(String... args) {
try {
log.info("");
log.info("╔═══════════════════════════════════════════════════════════╗");
log.info("║                                                           ║");
log.info("║   🌱 STARTING DATA SEEDING PROCESS...                    ║");
log.info("║                                                           ║");
log.info("║   Please wait while we populate the database with        ║");
log.info("║   initial data for testing...                            ║");
log.info("║                                                           ║");
log.info("╚══════════════════════════════════════════════════════════╝");
log.info("");



log.info("📍 [1/7] Seeding locations... [SKIPPED - Use SQL script to seed locations]");


log.info("   ✅ Locations seeding skipped (use SQL script)");

log.info("🌾 [2/7] Seeding crop types...");
seedCropTypesIfEmpty();
cropTypeRepository.flush();
log.info("   ✅ Crop types completed");

log.info("👤 [3/7] Seeding admin user...");
seedAdminUserIfNotExists();
userRepository.flush();
userProfileRepository.flush();
log.info("   ✅ Admin user completed");

log.info("🏭 [4/7] Seeding warehouses...");
seedWarehousesIfEmpty();
warehouseRepository.flush();
log.info("   ✅ Warehouses completed");

log.info("👥 [5/7] Seeding test users (farmers, buyers, storekeepers)...");
seedTestUsers();
userRepository.flush();
userProfileRepository.flush();
log.info("   ✅ Test users completed");

log.info("🔐 [6/6] Assigning warehouse managers...");
seedWarehouseManagers();
warehouseAccessRepository.flush();
log.info("   ✅ Warehouse managers completed");



log.info("📦 [SKIPPED] Inventory seeding - Farmers will add crops from scratch with proper information");
log.info("💰 [SKIPPED] Transaction seeding - Transactions will be created through normal purchase flow");

log.info("");
log.info("═══════════════════════════════════════════════════════════");
log.info("✅ DATA SEEDING PROCESS COMPLETED SUCCESSFULLY!");
log.info("═══════════════════════════════════════════════════════════");
log.info("");


log.info("═══════════════════════════════════════════════════════════");
log.info("  🚀 Rangira Agro Farming Application is READY!");
log.info("  📚 API Documentation: http://localhost:8080/swagger-ui.html");
log.info("  🌐 Server running on: http://localhost:8080");
log.info("═══════════════════════════════════════════════════════════");
log.info("  LOGIN CREDENTIALS:");
log.info("  Admin: ngaboangelos2@gmail.com / Ngabo@123");
log.info("  Farmers: farmer1@test.com to farmer25@test.com / Test@123");
log.info("  Buyers: buyer1@test.com to buyer20@test.com / Test@123");
log.info("  Storekeepers: storekeeper1@test.com to storekeeper12@test.com / Test@123");
log.info("═══════════════════════════════════════════════════════════");
log.info("  FINAL DATA SUMMARY (After Seeding):");
try {
long cropCount = cropTypeRepository.count();
long warehouseCount = warehouseRepository.count();
long totalUsers = userRepository.count();
long farmers = userRepository.findByUserType(UserType.FARMER).size();
long buyers = userRepository.findByUserType(UserType.BUYER).size();
long storekeepers = userRepository.findByUserType(UserType.STOREKEEPER).size();
long inventoryCount = inventoryRepository.count();
long transactionCount = transactionRepository.count();

log.info("  ✅ {} Crop Types", cropCount);
log.info("  ✅ {} Warehouses", warehouseCount);
log.info("  ✅ {} Users (1 Admin + {} Farmers + {} Buyers + {} Storekeepers)",
totalUsers, farmers, buyers, storekeepers);
log.info("  ℹ️  {} Inventory Items (Farmers will add crops from scratch)", inventoryCount);
log.info("  ℹ️  {} Transactions (Will be created through normal flow)", transactionCount);


if (cropCount < 30) {
log.warn("  ⚠️  Crop Types: Expected 30, found {}. Some may have failed to seed.", cropCount);
}
if (warehouseCount < 12) {
log.warn("  ⚠️  Warehouses: Expected 12, found {}. Some may have failed to seed.", warehouseCount);
}
if (farmers < 25) {
log.warn("  ⚠️  Farmers: Expected 25, found {}. Some may have failed to seed.", farmers);
}
if (buyers < 20) {
log.warn("  ⚠️  Buyers: Expected 20, found {}. Some may have failed to seed.", buyers);
}
if (storekeepers < 12) {
log.warn("  ⚠️  Storekeepers: Expected 12, found {}. Some may have failed to seed.", storekeepers);
}

if (inventoryCount > 0) {
log.info("  ℹ️  Note: {} existing inventory items found (not seeded)", inventoryCount);
}
if (transactionCount > 0) {
log.info("  ℹ️  Note: {} existing transactions found (not seeded)", transactionCount);
}
} catch (Exception e) {
log.error("Could not generate complete summary: {}", e.getMessage(), e);
}
log.info("═══════════════════════════════════════════════════════════");


log.info("");
log.info("╔═══════════════════════════════════════════════════════════╗");
log.info("║                                                           ║");
log.info("║   ✅ SYSTEM STARTUP COMPLETE - READY FOR USE!            ║");
log.info("║                                                           ║");
log.info("║   All data has been seeded and verified.                ║");
log.info("║   The application is now running and ready to accept     ║");
log.info("║   requests from the frontend.                            ║");
log.info("║                                                           ║");
log.info("╚═══════════════════════════════════════════════════════════╝");
log.info("");
} catch (Exception e) {
log.error("❌ CRITICAL ERROR during data seeding: ", e);
log.warn("Some data may not have been seeded. Check logs for details.");
log.info("Application will continue running despite seeding errors.");


log.info("");
log.info("╔═══════════════════════════════════════════════════════════╗");
log.info("║                                                           ║");
log.info("║   ⚠️  SYSTEM STARTED WITH WARNINGS                        ║");
log.info("║                                                           ║");
log.info("║   The application is running but some data seeding        ║");
log.info("║   operations may have failed. Check logs above for        ║");
log.info("║   details.                                                 ║");
log.info("║                                                           ║");
log.info("╚═══════════════════════════════════════════════════════════╝");
log.info("");
}
}

private void safeExecute(String operationName, Runnable operation) {
try {
operation.run();
} catch (Exception e) {
log.error("Error in {}: {}", operationName, e.getMessage(), e);
log.warn("Continuing with next seeding operation...");
}
}

private void seedLocationsIfEmpty() {
safeExecute("seedLocationsIfEmpty", () -> {
log.info("Starting comprehensive Rwandan location seeding...");
seedRwandanLocations();
log.info("Completed Rwandan location seeding");
log.info("Ensuring all sectors have cells and villages...");
ensureAllSectorsHaveCellsAndVillages();
log.info("Completed cells and villages verification");
});
}

private void ensureAllSectorsHaveCellsAndVillages() {
Map<String, List<String>> cellsBySector = RwandanLocationData.getCellsBySector();
Map<String, List<String>> villagesByCell = RwandanLocationData.getVillagesByCell();
Set<String> usedCodes = new HashSet<>();

List<Location> allSectors = locationRepository.findByTypeWithParents(ELocation.Sector);
log.info("Found {} sectors in database. Ensuring all have cells and villages...", allSectors.size());
log.info("Total cell keys available in RwandanLocationData: {}", cellsBySector.size());
log.info("Total village keys available in RwandanLocationData: {}", villagesByCell.size());

if (allSectors.isEmpty()) {
log.warn("No sectors found in database! Cannot seed cells and villages.");
return;
}

int cellsAdded = 0;
int villagesAdded = 0;
int sectorsProcessed = 0;
int sectorsSkipped = 0;
int cellsSkipped = 0;
int villagesSkipped = 0;

for (Location sector : allSectors) {
try {
if (sector.getParent() == null) {
log.warn("Sector {} has no parent, skipping", sector.getName());
sectorsSkipped++;
continue;
}

Location district = sector.getParent();
if (district.getParent() == null) {
log.warn("District {} (parent of sector {}) has no parent, skipping", district.getName(), sector.getName());
sectorsSkipped++;
continue;
}

String districtName = district.getName().replace(" District", "").trim();
String sectorName = sector.getName().replace(" Sector", "").trim();
String cellKey = districtName + "-" + sectorName;

log.debug("Processing sector: {} in district: {} (key: {})", sectorName, districtName, cellKey);

List<String> cellNames = cellsBySector.get(cellKey);
if (cellNames == null || cellNames.isEmpty()) {
log.warn("No cells data found for sector key: {} (Sector: {}, District: {})", cellKey, sectorName, districtName);
sectorsSkipped++;
continue;
}

log.info("Found {} cells for sector key: {} (Sector: {})", cellNames.size(), cellKey, sectorName);

for (String cellName : cellNames) {
if (cellName == null || cellName.trim().isEmpty()) {
cellsSkipped++;
continue;
}

String cleanCellName = cellName.trim().replace("_", "");
String cellDisplayName = cleanCellName;
if (!cellDisplayName.endsWith(" Cell")) {
cellDisplayName = cellDisplayName + " Cell";
}

String cellCode = generateUniqueCode("CELL", cleanCellName + sectorName, usedCodes, 20);
Location existingCell = locationRepository.findByCode(cellCode).orElse(null);

if (existingCell == null) {
Location cell = getOrCreateCell(cellCode, cellDisplayName, sector);
cellsAdded++;
log.debug("Created cell: {} in sector: {}", cellDisplayName, sectorName);

String villageKey = districtName + "-" + sectorName + "-" + cleanCellName;
List<String> villageNames = villagesByCell.get(villageKey);

if (villageNames != null && !villageNames.isEmpty()) {
log.debug("Found {} villages for cell key: {}", villageNames.size(), villageKey);
for (String villageName : villageNames) {
if (villageName == null || villageName.trim().isEmpty()) {
villagesSkipped++;
continue;
}

String villageDisplayName = villageName.trim();
String villageCode = generateUniqueCode("VIL", villageDisplayName + cleanCellName + sectorName, usedCodes, 20);
Location existingVillage = locationRepository.findByCode(villageCode).orElse(null);

if (existingVillage == null) {
getOrCreateVillage(villageCode, villageDisplayName, cell);
villagesAdded++;
} else {
villagesSkipped++;
}
}
} else {
log.debug("No villages found for cell key: {}", villageKey);
}
} else {
String villageKey = districtName + "-" + sectorName + "-" + cleanCellName;
List<String> villageNames = villagesByCell.get(villageKey);

if (villageNames != null && !villageNames.isEmpty()) {
List<Location> existingVillages = locationRepository.findByParentId(existingCell.getId());
Set<String> existingVillageNames = existingVillages.stream()
.map(Location::getName)
.map(name -> name.replace(" Village", "").trim())
.collect(java.util.stream.Collectors.toSet());

for (String villageName : villageNames) {
if (villageName == null || villageName.trim().isEmpty()) {
villagesSkipped++;
continue;
}

String villageDisplayName = villageName.trim();
if (!existingVillageNames.contains(villageDisplayName)) {
String villageCode = generateUniqueCode("VIL", villageDisplayName + cleanCellName + sectorName, usedCodes, 20);
Location existingVillage = locationRepository.findByCode(villageCode).orElse(null);

if (existingVillage == null) {
getOrCreateVillage(villageCode, villageDisplayName, existingCell);
villagesAdded++;
} else {
villagesSkipped++;
}
} else {
villagesSkipped++;
}
}
}
}
}

sectorsProcessed++;
if (sectorsProcessed % 50 == 0) {
log.info("Processed {} sectors, added {} cells and {} villages so far...", sectorsProcessed, cellsAdded, villagesAdded);
}
} catch (Exception e) {
log.error("Error processing sector {}: {}", sector.getName(), e.getMessage(), e);
sectorsSkipped++;
}

if ((cellsAdded + villagesAdded) % 100 == 0 && (cellsAdded + villagesAdded) > 0) {
entityManager.flush();
}
}

entityManager.flush();
log.info("Processed {} sectors, skipped {} sectors", sectorsProcessed, sectorsSkipped);
log.info("Added {} new cells ({} skipped) and {} new villages ({} skipped) to existing sectors",
cellsAdded, cellsSkipped, villagesAdded, villagesSkipped);

long totalCells = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Cell)
.count();
long totalVillages = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Village)
.count();
log.info("Total cells in database: {}, Total villages in database: {}", totalCells, totalVillages);

if (totalCells == 0) {
log.error("CRITICAL: No cells found in database! The seeding may have failed.");
}
if (totalVillages == 0) {
log.error("CRITICAL: No villages found in database! The seeding may have failed.");
}

if (sectorsSkipped > 0) {
log.warn("WARNING: {} sectors were skipped. This might indicate missing data in RwandanLocationData or incorrect key matching.", sectorsSkipped);
}
}

private void seedRwandanLocations() {
Map<String, Location> provinces = new HashMap<>();
Map<String, Location> districts = new HashMap<>();
Map<String, Location> sectors = new HashMap<>();
Set<String> usedCodes = new HashSet<>();

int sectorCount = 0;
int cellCount = 0;
int villageCount = 0;

Location kigali = getOrCreateProvince("PRV-KGL", "Kigali");
Location northern = getOrCreateProvince("PRV-NORTH", "Northern Province");
Location southern = getOrCreateProvince("PRV-SOUTH", "Southern Province");
Location eastern = getOrCreateProvince("PRV-EAST", "Eastern Province");
Location western = getOrCreateProvince("PRV-WEST", "Western Province");

provinces.put("Kigali", kigali);
provinces.put("Northern Province", northern);
provinces.put("Southern Province", southern);
provinces.put("Eastern Province", eastern);
provinces.put("Western Province", western);

Map<String, List<String>> districtsByProvince = RwandanLocationData.getDistrictsByProvince();
Map<String, List<String>> sectorsByDistrict = RwandanLocationData.getSectorsByDistrict();
Map<String, List<String>> cellsBySector = RwandanLocationData.getCellsBySector();
Map<String, List<String>> villagesByCell = RwandanLocationData.getVillagesByCell();

for (Map.Entry<String, List<String>> provinceEntry : districtsByProvince.entrySet()) {
String provinceKey = provinceEntry.getKey();
Location province = provinces.get(provinceKey);
if (province == null) {
log.warn("Province not found in provinces map: {}", provinceKey);
continue;
}
log.info("Processing province: {} with {} districts", provinceKey, provinceEntry.getValue().size());

for (String districtName : provinceEntry.getValue()) {
String districtCode = generateUniqueCode("DST", districtName, usedCodes, 10);
Location district = getOrCreateDistrict(districtCode, districtName + " District", province);
districts.put(districtName, district);
log.debug("Created district: {} in province: {} (parent ID: {})", districtName + " District", provinceKey, district.getParent() != null ? district.getParent().getId() : "NULL");

List<String> sectorNames = sectorsByDistrict.get(districtName);
if (sectorNames != null && !sectorNames.isEmpty()) {
log.info("Processing district: {} (Province: {}) with {} sectors", districtName, provinceKey, sectorNames.size());
for (String sectorName : sectorNames) {
String sectorCode = generateUniqueCode("SCTR", sectorName, usedCodes, 15);
Location sector = getOrCreateSector(sectorCode, sectorName + " Sector", district);
String sectorKey = districtName + "-" + sectorName;
sectors.put(sectorKey, sector);
sectorCount++;

String cellKey = sectorKey;
List<String> cellNames = cellsBySector.get(cellKey);

if (cellNames != null && !cellNames.isEmpty() && !cellNames.stream().allMatch(s -> s == null || s.trim().isEmpty())) {
log.debug("Processing sector: {} with {} cells", sectorName, cellNames.size());
for (String cellName : cellNames) {
if (cellName == null || cellName.trim().isEmpty()) continue;

String cleanCellName = cellName.trim().replace("_", "");
String cellDisplayName = cleanCellName;
if (!cellDisplayName.endsWith(" Cell")) {
cellDisplayName = cellDisplayName + " Cell";
}

String cellCode = generateUniqueCode("CELL", cleanCellName + sectorName, usedCodes, 20);
Location existingCell = locationRepository.findByCode(cellCode).orElse(null);
if (existingCell == null) {
Location cell = getOrCreateCell(cellCode, cellDisplayName, sector);
cellCount++;

String villageKey = districtName + "-" + sectorName + "-" + cleanCellName;
List<String> villageNames = villagesByCell.get(villageKey);

if (villageNames != null && !villageNames.isEmpty()) {
for (String villageName : villageNames) {
if (villageName == null || villageName.trim().isEmpty()) continue;
String villageDisplayName = villageName.trim();
String villageCode = generateUniqueCode("VIL", villageDisplayName + cleanCellName + sectorName, usedCodes, 20);
Location existingVillage = locationRepository.findByCode(villageCode).orElse(null);
if (existingVillage == null) {
getOrCreateVillage(villageCode, villageDisplayName, cell);
villageCount++;
}
}
}
}
}
} else {
log.warn("No cells found for sector: {} in district: {} (key: {})", sectorName, districtName, cellKey);
}

entityManager.flush();
}
}
}
}

entityManager.flush();
log.info("Seeded Rwandan locations: 5 Provinces, 30 Districts, {} Sectors, {} Cells, {} Villages",
sectorCount, cellCount, villageCount);

long sectorCountInDb = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Sector)
.count();
long cellCountInDb = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Cell)
.count();
long villageCountInDb = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Village)
.count();
log.info("Verified {} sectors, {} cells, {} villages in database", sectorCountInDb, cellCountInDb, villageCountInDb);
}

private String generateUniqueCode(String prefix, String name, Set<String> usedCodes, int maxLength) {
String baseCode = prefix + "-" + name.toUpperCase().replaceAll("\\s+", "");
String code = baseCode.substring(0, Math.min(maxLength, baseCode.length()));

int counter = 1;
String finalCode = code;
while (usedCodes.contains(finalCode)) {
String suffix = String.valueOf(counter);
int availableLength = maxLength - suffix.length() - 1;
finalCode = code.substring(0, Math.min(availableLength, code.length())) + "-" + suffix;
counter++;
}
usedCodes.add(finalCode);
return finalCode;
}


private Location getOrCreateProvince(String code, String name) {
return locationRepository.findByCode(code)
.orElseGet(() -> createProvince(code, name));
}

private Location getOrCreateDistrict(String code, String name, Location province) {
return locationRepository.findByCode(code)
.map(existing -> {
if (existing.getParent() == null || !existing.getParent().getId().equals(province.getId())) {
log.warn("District {} already exists but has wrong parent. Updating parent from {} to {}",
name, existing.getParent() != null ? existing.getParent().getName() : "NULL", province.getName());
existing.setParent(province);
return locationRepository.save(existing);
}
return existing;
})
.orElseGet(() -> createDistrict(code, name, province));
}

private Location getOrCreateSector(String code, String name, Location district) {
return locationRepository.findByCode(code)
.orElseGet(() -> createSector(code, name, district));
}

private Location getOrCreateCell(String code, String name, Location sector) {
return locationRepository.findByCode(code)
.orElseGet(() -> createCell(code, name, sector));
}

private Location getOrCreateVillage(String code, String name, Location cell) {
return locationRepository.findByCode(code)
.orElseGet(() -> createVillage(code, name, cell));
}


private Location createProvince(String code, String name) {
Location province = Location.builder()
.code(code)
.name(name)
.type(ELocation.Province)
.build();
Location saved = locationRepository.save(province);
entityManager.flush();
return saved;
}

private Location createDistrict(String code, String name, Location province) {
Location district = Location.builder()
.code(code)
.name(name)
.type(ELocation.District)
.parent(province)
.build();
Location saved = locationRepository.save(district);
entityManager.flush();
return saved;
}

private Location createSector(String code, String name, Location district) {
Location sector = Location.builder()
.code(code)
.name(name)
.type(ELocation.Sector)
.parent(district)
.build();
Location saved = locationRepository.save(sector);
entityManager.flush();
return saved;
}

private Location createCell(String code, String name, Location sector) {
Location cell = Location.builder()
.code(code)
.name(name)
.type(ELocation.Cell)
.parent(sector)
.build();
Location saved = locationRepository.save(cell);
entityManager.flush();
return saved;
}

private Location createVillage(String code, String name, Location cell) {
Location village = Location.builder()
.code(code)
.name(name)
.type(ELocation.Village)
.parent(cell)
.build();
Location saved = locationRepository.save(village);
entityManager.flush();
return saved;
}

private void seedCropTypesIfEmpty() {
safeExecute("seedCropTypesIfEmpty", () -> {
long existingCount = cropTypeRepository.count();
log.info("Current crop types in database: {}", existingCount);

if (existingCount >= 30) {
log.info("Crop types already exist ({}), skipping seed", existingCount);
return;
}
if (existingCount > 0) {
log.warn("Only {} crop types found, expected 30. Seeding additional crop types...", existingCount);
} else {
log.info("No crop types found. Seeding all 30 crop types...");
}

int created = 0;


log.info("Seeding cereals...");
created += saveCropIfNotExists("CRP-MAI", "Maize", CropCategory.CEREALS, new BigDecimal("800")) ? 1 : 0;
created += saveCropIfNotExists("CRP-RIC", "Rice", CropCategory.CEREALS, new BigDecimal("1500")) ? 1 : 0;
created += saveCropIfNotExists("CRP-WHE", "Wheat", CropCategory.CEREALS, new BigDecimal("1000")) ? 1 : 0;
created += saveCropIfNotExists("CRP-SOR", "Sorghum", CropCategory.CEREALS, new BigDecimal("700")) ? 1 : 0;
created += saveCropIfNotExists("CRP-MIL", "Millet", CropCategory.CEREALS, new BigDecimal("900")) ? 1 : 0;
created += saveCropIfNotExists("CRP-BAR", "Barley", CropCategory.CEREALS, new BigDecimal("850")) ? 1 : 0;


log.info("Seeding legumes...");
created += saveCropIfNotExists("CRP-BEA", "Beans", CropCategory.LEGUMES, new BigDecimal("1200")) ? 1 : 0;
created += saveCropIfNotExists("CRP-PEA", "Peas", CropCategory.LEGUMES, new BigDecimal("1100")) ? 1 : 0;
created += saveCropIfNotExists("CRP-SOY", "Soybeans", CropCategory.LEGUMES, new BigDecimal("1300")) ? 1 : 0;
created += saveCropIfNotExists("CRP-GRO", "Groundnuts", CropCategory.LEGUMES, new BigDecimal("1400")) ? 1 : 0;
created += saveCropIfNotExists("CRP-LEN", "Lentils", CropCategory.LEGUMES, new BigDecimal("1250")) ? 1 : 0;


log.info("Seeding tubers...");
created += saveCropIfNotExists("CRP-POT", "Potatoes", CropCategory.TUBERS, new BigDecimal("600")) ? 1 : 0;
created += saveCropIfNotExists("CRP-CAS", "Cassava", CropCategory.TUBERS, new BigDecimal("500")) ? 1 : 0;
created += saveCropIfNotExists("CRP-SWE", "Sweet Potatoes", CropCategory.TUBERS, new BigDecimal("550")) ? 1 : 0;
created += saveCropIfNotExists("CRP-YAM", "Yams", CropCategory.TUBERS, new BigDecimal("650")) ? 1 : 0;


log.info("Seeding vegetables...");
created += saveCropIfNotExists("CRP-TOM", "Tomatoes", CropCategory.VEGETABLES, new BigDecimal("2000")) ? 1 : 0;
created += saveCropIfNotExists("CRP-CAB", "Cabbage", CropCategory.VEGETABLES, new BigDecimal("800")) ? 1 : 0;
created += saveCropIfNotExists("CRP-ONI", "Onions", CropCategory.VEGETABLES, new BigDecimal("1800")) ? 1 : 0;
created += saveCropIfNotExists("CRP-CAR", "Carrots", CropCategory.VEGETABLES, new BigDecimal("1600")) ? 1 : 0;
created += saveCropIfNotExists("CRP-SPI", "Spinach", CropCategory.VEGETABLES, new BigDecimal("1500")) ? 1 : 0;
created += saveCropIfNotExists("CRP-LET", "Lettuce", CropCategory.VEGETABLES, new BigDecimal("1700")) ? 1 : 0;
created += saveCropIfNotExists("CRP-CUC", "Cucumber", CropCategory.VEGETABLES, new BigDecimal("1400")) ? 1 : 0;
created += saveCropIfNotExists("CRP-PEP", "Peppers", CropCategory.VEGETABLES, new BigDecimal("2200")) ? 1 : 0;
created += saveCropIfNotExists("CRP-EGG", "Eggplant", CropCategory.VEGETABLES, new BigDecimal("1300")) ? 1 : 0;
created += saveCropIfNotExists("CRP-OKR", "Okra", CropCategory.VEGETABLES, new BigDecimal("1200")) ? 1 : 0;


log.info("Seeding fruits...");
created += saveCropIfNotExists("CRP-BAN", "Bananas", CropCategory.FRUITS, new BigDecimal("1200")) ? 1 : 0;
created += saveCropIfNotExists("CRP-AVO", "Avocados", CropCategory.FRUITS, new BigDecimal("2500")) ? 1 : 0;
created += saveCropIfNotExists("CRP-MAN", "Mangoes", CropCategory.FRUITS, new BigDecimal("1800")) ? 1 : 0;
created += saveCropIfNotExists("CRP-PAP", "Papayas", CropCategory.FRUITS, new BigDecimal("1500")) ? 1 : 0;
created += saveCropIfNotExists("CRP-PIN", "Pineapples", CropCategory.FRUITS, new BigDecimal("1600")) ? 1 : 0;
created += saveCropIfNotExists("CRP-PAS", "Passion Fruit", CropCategory.FRUITS, new BigDecimal("2000")) ? 1 : 0;


entityManager.flush();

long cropCount = cropTypeRepository.count();
log.info("✅ Created {} new crop types. Total crop types in DB: {}", created, cropCount);

if (cropCount == 0) {
log.error("❌ CRITICAL: No crop types were saved! This indicates a serious problem.");
log.error("Please check database connection and entity configuration.");
} else if (cropCount < 30) {
log.warn("⚠️ Only {} crop types found, expected 30. {} may have failed to create.", cropCount, (30 - cropCount));
} else {
log.info("✅ Successfully seeded {} crop types!", cropCount);
}
});
}

private boolean saveCropIfNotExists(String code, String name, CropCategory category, BigDecimal pricePerKg) {
try {

if (cropTypeRepository.findByCropCode(code).isPresent()) {
log.debug("Crop type {} already exists, skipping", code);
return false;
}

CropType crop = newCrop(code, name, category, pricePerKg);
CropType saved = cropTypeRepository.save(crop);
entityManager.flush();


if (saved.getId() == null) {
log.error("Crop type {} was saved but has no ID!", code);
return false;
}

log.info("✅ Created crop type: {} ({}) with ID: {}", name, code, saved.getId());
return true;
} catch (Exception e) {
log.error("❌ Failed to create crop type {} ({}): {}", code, name, e.getMessage(), e);
return false;
}
}

private CropType newCrop(String code, String name, CropCategory category, BigDecimal pricePerKg) {
CropType ct = new CropType();
ct.setCropCode(code);
ct.setCropName(name);
ct.setCategory(category);
ct.setMeasurementUnit(MeasurementUnit.KG);
ct.setDescription("High quality " + name.toLowerCase() + " from Rwanda");
ct.setPricePerKg(pricePerKg);
return ct;
}


private void seedAdminUserIfNotExists() {
safeExecute("seedAdminUserIfNotExists", () -> {
String adminEmail = "ngaboangelos2@gmail.com";
String adminPassword = "Ngabo@123";


User existingAdmin = userRepository.findByEmail(adminEmail).orElse(null);

if (existingAdmin != null) {

log.info("Admin user exists, updating password to ensure correct hash");
existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
existingAdmin.setUserType(UserType.ADMIN);
existingAdmin.setStatus(UserStatus.ACTIVE);
userRepository.save(existingAdmin);


UserProfile profile = existingAdmin.getUserProfile();
if (profile == null) {
profile = new UserProfile();
profile.setUser(existingAdmin);
}
profile.setVerified(true);
userProfileRepository.save(profile);

log.info("Admin user password updated: {}", adminEmail);
return;
}


Location sectorKigali = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Sector &&
l.getName().contains("Bumbogo"))
.findFirst()
.orElseGet(() -> locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Sector)
.findFirst()
.orElse(null));

if (sectorKigali == null) {
log.warn("No sector location found. Cannot seed admin user.");
return;
}


User adminUser = createUser("ADM-001", "Admin", "User", adminEmail,
"+250788000001", adminPassword, UserType.ADMIN, UserStatus.ACTIVE, sectorKigali, true);

if (adminUser != null) {
log.info("Admin user created successfully: {}", adminEmail);
} else {
log.error("Failed to create admin user - check for conflicts");
}
});
}

private User createUser(String userCode, String firstName, String lastName, String email,
String phoneNumber, String password, UserType userType, UserStatus status,
Location location, boolean verified) {
try {
if (userRepository.existsByUserCode(userCode) ||
userRepository.existsByEmail(email) ||
userRepository.existsByPhoneNumber(phoneNumber)) {
log.debug("User already exists: userCode={}, email={}, phone={}", userCode, email, phoneNumber);
return null;
}

User user = new User();
user.setUserCode(userCode);
user.setFirstName(firstName);
user.setLastName(lastName);
user.setEmail(email);
user.setPhoneNumber(phoneNumber);

user.setPassword(passwordEncoder.encode(password));
user.setUserType(userType);
user.setStatus(status);
user.setLocation(location);
user = userRepository.save(user);


entityManager.flush();

UserProfile profile = new UserProfile();
profile.setUser(user);
profile.setVerified(verified);
userProfileRepository.save(profile);
entityManager.flush();

log.debug("Created user: {} ({})", email, userType);
return user;
} catch (Exception e) {
log.error("Failed to create user {}: {}", email, e.getMessage(), e);
return null;
}
}

private void seedWarehousesIfEmpty() {
safeExecute("seedWarehousesIfEmpty", () -> {
long existingCount = warehouseRepository.count();
if (existingCount >= 12) {
log.info("Warehouses already exist ({}), skipping seed", existingCount);
return;
}
if (existingCount > 0) {
log.warn("Only {} warehouses found, expected 12. Seeding additional warehouses...", existingCount);
}


List<Location> sectors = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Sector)
.collect(Collectors.toList());

log.info("Found {} sectors in database", sectors.size());
if (sectors.isEmpty()) {
log.error("❌ No sectors found, cannot seed warehouses. Please check location seeding.");
return;
}


int created = 0;
created += createWarehouse("WH-KIM-001", "Kimironko Central Warehouse",
WarehouseType.COOPERATIVE, new BigDecimal("50000"), sectors.get(0)) ? 1 : 0;
created += createWarehouse("WH-REM-001", "Remera Storage Warehouse",
WarehouseType.PRIVATE, new BigDecimal("30000"), sectors.get(Math.min(1, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-KIN-001", "Kinigi Agricultural Warehouse",
WarehouseType.GOVERNMENT, new BigDecimal("75000"), sectors.get(Math.min(2, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-RUK-001", "Rukara Cooperative Warehouse",
WarehouseType.COOPERATIVE, new BigDecimal("40000"), sectors.get(Math.min(3, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-NYA-001", "Nyabugogo Main Warehouse",
WarehouseType.GOVERNMENT, new BigDecimal("100000"), sectors.get(Math.min(4, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-GAS-001", "Gasabo District Warehouse",
WarehouseType.COOPERATIVE, new BigDecimal("60000"), sectors.get(Math.min(5, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-KIC-001", "Kicukiro Storage Facility",
WarehouseType.PRIVATE, new BigDecimal("35000"), sectors.get(Math.min(6, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-NYA-002", "Nyarugenge Central Warehouse",
WarehouseType.GOVERNMENT, new BigDecimal("80000"), sectors.get(Math.min(7, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-MUS-001", "Musanze Regional Warehouse",
WarehouseType.COOPERATIVE, new BigDecimal("55000"), sectors.get(Math.min(8, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-RUB-001", "Rubavu Storage Center",
WarehouseType.PRIVATE, new BigDecimal("45000"), sectors.get(Math.min(9, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-KAY-001", "Kayonza Agricultural Depot",
WarehouseType.COOPERATIVE, new BigDecimal("70000"), sectors.get(Math.min(10, sectors.size()-1))) ? 1 : 0;
created += createWarehouse("WH-HUY-001", "Huye District Warehouse",
WarehouseType.GOVERNMENT, new BigDecimal("65000"), sectors.get(Math.min(11, sectors.size()-1))) ? 1 : 0;

long warehouseCount = warehouseRepository.count();
log.info("✅ Created {} new warehouses. Total warehouses in DB: {}", created, warehouseCount);

if (warehouseCount == 0) {
log.error("❌ WARNING: No warehouses were saved! Check for errors above.");
} else if (warehouseCount < 12) {
log.warn("⚠️ Only {} warehouses found, expected 12. Some may have failed to create.", warehouseCount);
}
});
}

private boolean createWarehouse(String code, String name, WarehouseType type,
BigDecimal capacity, Location location) {
try {

if (warehouseRepository.findByWarehouseCode(code).isPresent()) {
log.debug("Warehouse {} already exists, skipping", code);
return false;
}

StorageWarehouse warehouse = new StorageWarehouse();
warehouse.setWarehouseCode(code);
warehouse.setWarehouseName(name);
warehouse.setWarehouseType(type);
warehouse.setTotalCapacityKg(capacity);
warehouse.setAvailableCapacityKg(capacity);
warehouse.setStatus(WarehouseStatus.ACTIVE);
warehouse.setLocation(location);
warehouseRepository.save(warehouse);
entityManager.flush();
log.debug("Created warehouse: {} ({})", name, code);
return true;
} catch (Exception e) {
log.error("Failed to create warehouse {}: {}", code, e.getMessage(), e);
return false;
}
}


private void seedTestUsers() {
safeExecute("seedTestUsers", () -> {

long existingFarmers = userRepository.findByUserType(UserType.FARMER).size();
long existingBuyers = userRepository.findByUserType(UserType.BUYER).size();
long existingStorekeepers = userRepository.findByUserType(UserType.STOREKEEPER).size();

log.info("Existing users - Farmers: {}, Buyers: {}, Storekeepers: {}",
existingFarmers, existingBuyers, existingStorekeepers);


if (existingFarmers >= 25 && existingBuyers >= 20 && existingStorekeepers >= 12) {
log.info("Test users already exist in sufficient numbers, skipping seed");
return;
}

log.info("Starting to seed test users (Farmers, Buyers, Storekeepers)...");

Location sectorKigali = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Sector)
.findFirst()
.orElse(null);

if (sectorKigali == null) {
log.warn("No sector found, cannot seed test users");
return;
}

String testPassword = "Test@123";
List<Location> allSectors = locationRepository.findAll().stream()
.filter(l -> l.getType() == ELocation.Sector)
.collect(Collectors.toList());

if (allSectors.isEmpty()) {
log.warn("No sectors found, cannot seed test users");
return;
}


String[] farmerFirstNames = {"Jean", "Marie", "Paul", "Claire", "Pierre", "Alice", "Robert", "Grace",
"David", "Sarah", "Joseph", "Francine", "Emmanuel", "Anastasie", "Theophile", "Clementine",
"Alexandre", "Immaculee", "Vincent", "Valerie", "Patrick", "Chantal", "Eric", "Diane", "Fabrice"};
String[] farmerLastNames = {"Baptiste", "Uwimana", "Mukamana", "Nyiramugisha", "Nkurunziza", "Mukamana",
"Nkurunziza", "Uwimana", "Mukamana", "Nyiramugisha", "Ntare", "Mukamana", "Nkurunziza",
"Uwimana", "Mukamana", "Nyiramugisha", "Ntare", "Mukamana", "Nkurunziza", "Uwimana",
"Mukamana", "Nyiramugisha", "Ntare", "Mukamana", "Nkurunziza"};

int farmerCount = 0;
for (int i = 0; i < 25; i++) {

String email = "farmer" + (i + 1) + "@test.com";
if (userRepository.findByEmail(email).isPresent()) {
log.debug("Farmer {} already exists, skipping", email);
continue;
}

Location sector = allSectors.get(i % allSectors.size());
String userCode = "FARM-" + String.format("%03d", i + 1);
String phone = "+2507881" + String.format("%05d", i + 1);
User created = createUser(userCode, farmerFirstNames[i], farmerLastNames[i], email, phone,
testPassword, UserType.FARMER, UserStatus.ACTIVE, sector, true);
if (created != null) {
farmerCount++;
}
}
log.info("Created {} farmers", farmerCount);


String[] buyerFirstNames = {"Alice", "Robert", "Grace", "David", "Sarah", "John", "Mary", "James",
"Linda", "Michael", "Patricia", "William", "Barbara", "Richard", "Elizabeth", "Joseph",
"Jennifer", "Thomas", "Maria", "Charles"};
String[] buyerLastNames = {"Mukamana", "Nkurunziza", "Uwimana", "Mukamana", "Nyiramugisha", "Ntare",
"Mukamana", "Nkurunziza", "Uwimana", "Mukamana", "Nyiramugisha", "Ntare", "Mukamana",
"Nkurunziza", "Uwimana", "Mukamana", "Nyiramugisha", "Ntare", "Mukamana", "Nkurunziza"};

int buyerCount = 0;
for (int i = 0; i < 20; i++) {

String email = "buyer" + (i + 1) + "@test.com";
if (userRepository.findByEmail(email).isPresent()) {
log.debug("Buyer {} already exists, skipping", email);
continue;
}

Location sector = allSectors.get((i + 25) % allSectors.size());
String userCode = "BUYR-" + String.format("%03d", i + 1);
String phone = "+2507882" + String.format("%05d", i + 1);
User created = createUser(userCode, buyerFirstNames[i], buyerLastNames[i], email, phone,
testPassword, UserType.BUYER, UserStatus.ACTIVE, sector, true);
if (created != null) {
buyerCount++;
}
}
log.info("Created {} buyers", buyerCount);


String[] storekeeperNames = {"Kimironko", "Remera", "Kinigi", "Rukara", "Nyabugogo", "Gasabo",
"Kicukiro", "Nyarugenge", "Musanze", "Rubavu", "Kayonza", "Huye"};

int storekeeperCount = 0;
for (int i = 0; i < 12; i++) {

String email = "storekeeper" + (i + 1) + "@test.com";
if (userRepository.findByEmail(email).isPresent()) {
log.debug("Storekeeper {} already exists, skipping", email);
continue;
}

Location sector = allSectors.get((i + 45) % allSectors.size());
String userCode = "STORE-" + String.format("%03d", i + 1);
String phone = "+2507883" + String.format("%05d", i + 1);
User created = createUser(userCode, "Manager", storekeeperNames[i], email, phone,
testPassword, UserType.STOREKEEPER, UserStatus.ACTIVE, sector, true);
if (created != null) {
storekeeperCount++;
}
}
log.info("Created {} storekeepers", storekeeperCount);
log.info("Total test users created: {} ({} farmers + {} buyers + {} storekeepers)",
farmerCount + buyerCount + storekeeperCount, farmerCount, buyerCount, storekeeperCount);

log.info("✅ Seeded test users: {} Farmers, {} Buyers, {} Storekeepers", farmerCount, buyerCount, storekeeperCount);


long totalFarmers = userRepository.findByUserType(UserType.FARMER).size();
long totalBuyers = userRepository.findByUserType(UserType.BUYER).size();
long totalStorekeepers = userRepository.findByUserType(UserType.STOREKEEPER).size();
log.info("✅ Verification - Total in DB: {} Farmers, {} Buyers, {} Storekeepers",
totalFarmers, totalBuyers, totalStorekeepers);
});
}


private void seedWarehouseManagers() {
safeExecute("seedWarehouseManagers", () -> {
long existingAccessCount = warehouseAccessRepository.count();
log.info("Existing warehouse access records: {}", existingAccessCount);


List<StorageWarehouse> warehouses = warehouseRepository.findAll();
List<User> storekeepers = userRepository.findByUserType(UserType.STOREKEEPER);

if (warehouses.isEmpty() || storekeepers.isEmpty()) {
log.warn("Cannot assign warehouse managers: warehouses or storekeepers missing");
return;
}


int assignedCount = 0;


for (int i = 0; i < Math.min(warehouses.size(), storekeepers.size()); i++) {
StorageWarehouse warehouse = warehouses.get(i);
User storekeeper = storekeepers.get(i);


boolean hasManager = warehouseAccessRepository.findAll().stream()
.anyMatch(wa -> wa.getWarehouse().getId().equals(warehouse.getId())
&& wa.getAccessLevel() == AccessLevel.MANAGER
&& wa.getIsActive());

if (hasManager) {
log.debug("Warehouse {} already has a manager, skipping", warehouse.getWarehouseName());
continue;
}


boolean alreadyAssigned = warehouseAccessRepository.findAll().stream()
.anyMatch(wa -> wa.getWarehouse().getId().equals(warehouse.getId())
&& wa.getUser().getId().equals(storekeeper.getId()));

if (alreadyAssigned) {
log.debug("Storekeeper {} already assigned to warehouse {}, skipping",
storekeeper.getEmail(), warehouse.getWarehouseName());
continue;
}

WarehouseAccess access = new WarehouseAccess();
access.setUser(storekeeper);
access.setWarehouse(warehouse);
access.setAccessLevel(AccessLevel.MANAGER);
access.setGrantedDate(java.time.LocalDate.now());
access.setIsActive(true);
access.setStatus(WarehouseAccessStatus.ACTIVE);

warehouseAccessRepository.save(access);
entityManager.flush();
assignedCount++;
log.info("Assigned {} as MANAGER to warehouse {}", storekeeper.getEmail(), warehouse.getWarehouseName());
}

log.info("Assigned {} new warehouse managers (total access records: {})",
assignedCount, warehouseAccessRepository.count());
});
}


@SuppressWarnings("unused")
private void seedTestInventory() {
safeExecute("seedTestInventory", () -> {
long existingCount = inventoryRepository.count();
log.info("Current inventory count: {}", existingCount);

if (existingCount >= 60) {
log.info("Test inventory already exists ({}), skipping seed", existingCount);
return;
}
if (existingCount > 0) {
log.info("Found {} existing inventory items. Will add new items to reach 60 total.", existingCount);
}

List<User> farmers = userRepository.findByUserType(UserType.FARMER);
List<User> storekeepers = userRepository.findByUserType(UserType.STOREKEEPER);
List<StorageWarehouse> warehouses = warehouseRepository.findAll();
List<CropType> cropTypes = cropTypeRepository.findAll();

if (farmers.isEmpty() || storekeepers.isEmpty() || warehouses.isEmpty() || cropTypes.isEmpty()) {
log.warn("Cannot seed inventory: missing required entities (farmers: {}, storekeepers: {}, warehouses: {}, cropTypes: {})",
farmers.size(), storekeepers.size(), warehouses.size(), cropTypes.size());
return;
}

int inventoryCount = 0;
int skippedCount = 0;
String[] qualityGrades = {"A", "B", "A+", "B+", "A", "A-", "B-", "A++"};


int nextInventoryNumber = 1;
for (int checkNum = 1; checkNum <= 999; checkNum++) {
String checkCode = "INV-" + String.format("%03d", checkNum);
if (!inventoryRepository.existsByInventoryCode(checkCode)) {
nextInventoryNumber = checkNum;
break;
}
}


int targetTotal = 60;
int itemsToCreate = (int) (targetTotal - existingCount);

log.info("Will create {} new inventory items (starting from INV-{:03d})", itemsToCreate, nextInventoryNumber);

for (int i = 0; i < itemsToCreate; i++) {
User farmer = farmers.get(i % farmers.size());
User storekeeper = storekeepers.get(i % storekeepers.size());
StorageWarehouse warehouse = warehouses.get(i % warehouses.size());
CropType cropType = cropTypes.get(i % cropTypes.size());


BigDecimal quantity = new BigDecimal(500 + (i % 10) * 500);


if (warehouse.getAvailableCapacityKg().compareTo(quantity) < 0) {
log.warn("Warehouse {} has insufficient capacity for quantity {}, skipping",
warehouse.getWarehouseName(), quantity);
skippedCount++;
continue;
}


String inventoryCode = "INV-" + String.format("%03d", nextInventoryNumber);
while (inventoryRepository.existsByInventoryCode(inventoryCode)) {
nextInventoryNumber++;
inventoryCode = "INV-" + String.format("%03d", nextInventoryNumber);
}
nextInventoryNumber++;

try {
Inventory inventory = new Inventory();
inventory.setInventoryCode(inventoryCode);
inventory.setFarmer(farmer);
inventory.setWarehouse(warehouse);
inventory.setCropType(cropType);
inventory.setStorekeeper(storekeeper);
inventory.setQuantityKg(quantity);
inventory.setRemainingQuantityKg(quantity);
inventory.setQualityGrade(qualityGrades[i % qualityGrades.length]);
inventory.setStorageDate(java.time.LocalDate.now().minusDays(60 - (i % 30)));
inventory.setExpectedWithdrawalDate(java.time.LocalDate.now().plusDays(30 + (i % 60)));
inventory.setStatus(InventoryStatus.STORED);
inventory.setNotes("Test inventory item " + inventoryCode + " - " + cropType.getCropName());

inventoryRepository.save(inventory);
entityManager.flush();
inventoryCount++;


BigDecimal currentAvailable = warehouse.getAvailableCapacityKg();
warehouse.setAvailableCapacityKg(currentAvailable.subtract(quantity));
warehouseRepository.save(warehouse);
warehouseRepository.flush();

log.debug("Created inventory: {} for farmer {} in warehouse {}",
inventoryCode, farmer.getEmail(), warehouse.getWarehouseName());
} catch (Exception e) {
log.error("Failed to create inventory {}: {}", inventoryCode, e.getMessage(), e);
skippedCount++;
}
}


long totalInventory = inventoryRepository.count();
log.info("✅ Seeded {} new inventory items ({} skipped). Total inventory in DB: {}",
inventoryCount, skippedCount, totalInventory);

if (totalInventory == 0) {
log.error("❌ WARNING: No inventory items in database! Check for errors above.");
} else if (totalInventory < 60) {
log.warn("⚠️ Only {} inventory items found, expected 60. Some may have failed to create.", totalInventory);
}
});
}


@SuppressWarnings("unused")
private void seedTestTransactions() {
safeExecute("seedTestTransactions", () -> {
long existingCount = transactionRepository.count();
log.info("Current transaction count: {}", existingCount);

if (existingCount >= 40) {
log.info("Test transactions already exist ({}), skipping seed", existingCount);
return;
}
if (existingCount > 0) {
log.info("Found {} existing transactions. Will add new transactions to reach 40 total.", existingCount);
}

int transactionsToCreate = (int) (40 - existingCount);

List<User> buyers = userRepository.findByUserType(UserType.BUYER);
List<Inventory> inventories = inventoryRepository.findAll();

if (buyers.isEmpty() || inventories.isEmpty()) {
log.warn("Cannot seed transactions: missing buyers or inventory");
return;
}

int transactionCount = 0;
int skippedCount = 0;


int nextTransactionNumber = 1;
for (int checkNum = 1; checkNum <= 999; checkNum++) {
String checkCode = "TXN-" + String.format("%03d", checkNum);
if (transactionRepository.findByTransactionCode(checkCode).isEmpty()) {
nextTransactionNumber = checkNum;
break;
}
}

log.info("Will create {} new transactions (starting from TXN-{:03d})", transactionsToCreate, nextTransactionNumber);


for (int i = 0; i < transactionsToCreate; i++) {
Inventory inventory = inventories.get(i % inventories.size());
User buyer = buyers.get(i % buyers.size());
User seller = inventory.getFarmer();
CropType cropType = inventory.getCropType();

if (cropType == null || cropType.getPricePerKg() == null) {
log.warn("Skipping transaction: crop type or price is null for inventory {}",
inventory.getInventoryCode());
continue;
}


BigDecimal quantity = new BigDecimal(50 + (i % 20) * 50);


if (inventory.getRemainingQuantityKg().compareTo(quantity) < 0) {

quantity = inventory.getRemainingQuantityKg();
if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
log.warn("Skipping transaction: inventory {} has no remaining quantity",
inventory.getInventoryCode());
continue;
}
}

BigDecimal unitPrice = cropType.getPricePerKg();
BigDecimal totalAmount = quantity.multiply(unitPrice);
BigDecimal storageFee = totalAmount.multiply(new BigDecimal("0.02"));
BigDecimal transactionFee = totalAmount.multiply(new BigDecimal("0.01"));
BigDecimal netAmount = totalAmount.subtract(storageFee).subtract(transactionFee);


String transactionCode = "TXN-" + String.format("%03d", nextTransactionNumber);
while (transactionRepository.findByTransactionCode(transactionCode).isPresent()) {
nextTransactionNumber++;
transactionCode = "TXN-" + String.format("%03d", nextTransactionNumber);
}
nextTransactionNumber++;


PaymentStatus paymentStatus;
DeliveryStatus deliveryStatus;
int statusPattern = i % 6;
switch (statusPattern) {
case 0:
paymentStatus = PaymentStatus.PAID;
deliveryStatus = DeliveryStatus.DELIVERED;
break;
case 1:
paymentStatus = PaymentStatus.PAID;
deliveryStatus = DeliveryStatus.PENDING;
break;
case 2:
paymentStatus = PaymentStatus.PENDING;
deliveryStatus = DeliveryStatus.DELIVERED;
break;
case 3:
paymentStatus = PaymentStatus.PENDING;
deliveryStatus = DeliveryStatus.PENDING;
break;
case 4:
paymentStatus = PaymentStatus.PAID;
deliveryStatus = DeliveryStatus.PENDING;
break;
default:
paymentStatus = PaymentStatus.PAID;
deliveryStatus = DeliveryStatus.DELIVERED;
break;
}

Transaction transaction = new Transaction();
transaction.setTransactionCode(transactionCode);
transaction.setInventory(inventory);
transaction.setBuyer(buyer);
transaction.setSeller(seller);
transaction.setQuantityKg(quantity);
transaction.setUnitPrice(unitPrice);
transaction.setTotalAmount(totalAmount);
transaction.setStorageFee(storageFee);
transaction.setTransactionFee(transactionFee);
transaction.setNetAmount(netAmount);
transaction.setPaymentStatus(paymentStatus);
transaction.setDeliveryStatus(deliveryStatus);
transaction.setTransactionDate(java.time.LocalDateTime.now().minusDays(30 - (i % 30)));
if (transaction.getPaymentStatus() == PaymentStatus.PAID) {
transaction.setPaymentDate(transaction.getTransactionDate().plusDays(1));
}
transaction.setNotes("Test transaction " + transactionCode + " - " + cropType.getCropName());

try {
transactionRepository.save(transaction);
entityManager.flush();
transactionCount++;
} catch (Exception e) {
log.error("Failed to create transaction {}: {}", transactionCode, e.getMessage(), e);
skippedCount++;
continue;
}


try {
BigDecimal remaining = inventory.getRemainingQuantityKg();
inventory.setRemainingQuantityKg(remaining.subtract(quantity));
if (inventory.getRemainingQuantityKg().compareTo(BigDecimal.ZERO) <= 0) {
inventory.setStatus(InventoryStatus.SOLD);
} else {
inventory.setStatus(InventoryStatus.PARTIALLY_SOLD);
}
inventoryRepository.save(inventory);
inventoryRepository.flush();
} catch (Exception e) {
log.error("Failed to update inventory after transaction: {}", e.getMessage(), e);
}
}


long totalTransactions = transactionRepository.count();
log.info("✅ Seeded {} new transactions ({} skipped). Total transactions in DB: {}",
transactionCount, skippedCount, totalTransactions);

if (totalTransactions == 0) {
log.error("❌ WARNING: No transactions in database! Check for errors above.");
} else if (totalTransactions < 40) {
log.warn("⚠️ Only {} transactions found, expected 40. Some may have failed to create.", totalTransactions);
}
});
}
}


