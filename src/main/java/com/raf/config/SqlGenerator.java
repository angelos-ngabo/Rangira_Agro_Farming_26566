package com.raf.config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SqlGenerator {

private static final String OUTPUT_FILE = "locations.sql";
private static final Map<String, String> idCache = new HashMap<>();

public static void main(String[] args) {
System.out.println("Generating SQL script...");

try (PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE))) {
writer.println("-- SQL Script to insert Rwandan Locations");
writer.println("-- Generated automatically");
writer.println();


writer.println("-- Provinces");
Map<String, String> provinces = new LinkedHashMap<>();
provinces.put("Kigali", "PRV-KGL");
provinces.put("Northern Province", "PRV-NORTH");
provinces.put("Southern Province", "PRV-SOUTH");
provinces.put("Eastern Province", "PRV-EAST");
provinces.put("Western Province", "PRV-WEST");

for (Map.Entry<String, String> entry : provinces.entrySet()) {
String name = entry.getKey();
String code = entry.getValue();
String id = getOrCreateId(code);
writer.printf(
"INSERT INTO location (id, code, name, type, parent_id) VALUES ('%s', '%s', '%s', 'Province', NULL) ON CONFLICT (code) DO NOTHING;%n",
id, code, escapeSql(name));
}


writer.println();
writer.println("-- Districts");
Map<String, List<String>> districtsByProvince = RwandanLocationData.getDistrictsByProvince();
for (Map.Entry<String, List<String>> entry : districtsByProvince.entrySet()) {
String provinceName = entry.getKey();
String provinceCode = provinces.get(provinceName);
if (provinceCode == null)
continue;
String provinceId = getOrCreateId(provinceCode);

for (String districtName : entry.getValue()) {
String code = generateCode("DST", districtName);
String id = getOrCreateId(code);
String fullName = districtName + " District";
writer.printf(
"INSERT INTO location (id, code, name, type, parent_id) VALUES ('%s', '%s', '%s', 'District', '%s') ON CONFLICT (code) DO NOTHING;%n",
id, code, escapeSql(fullName), provinceId);
}
}


writer.println();
writer.println("-- Sectors");
Map<String, List<String>> sectorsByDistrict = RwandanLocationData.getSectorsByDistrict();
for (Map.Entry<String, List<String>> entry : sectorsByDistrict.entrySet()) {
String districtName = entry.getKey();
String districtCode = generateCode("DST", districtName);
String districtId = getOrCreateId(districtCode);

for (String sectorName : entry.getValue()) {
String code = generateCode("SCTR", sectorName);
String id = getOrCreateId(code);
String fullName = sectorName + " Sector";
writer.printf(
"INSERT INTO location (id, code, name, type, parent_id) VALUES ('%s', '%s', '%s', 'Sector', '%s') ON CONFLICT (code) DO NOTHING;%n",
id, code, escapeSql(fullName), districtId);
}
}


writer.println();
writer.println("-- Cells");
Map<String, List<String>> cellsBySector = RwandanLocationData.getCellsBySector();
for (Map.Entry<String, List<String>> entry : cellsBySector.entrySet()) {
String key = entry.getKey();
String[] parts = key.split("-");
if (parts.length < 2)
continue;
String sectorName = parts[1];

String sectorCode = generateCode("SCTR", sectorName);
String sectorId = getOrCreateId(sectorCode);

for (String cellName : entry.getValue()) {
String cleanCellName = cellName.replace("_", "");
String code = generateCode("CELL", cleanCellName + sectorName);
String id = getOrCreateId(code);

String fullName = cleanCellName;
if (!fullName.endsWith(" Cell")) {
fullName += " Cell";
}

writer.printf(
"INSERT INTO location (id, code, name, type, parent_id) VALUES ('%s', '%s', '%s', 'Cell', '%s') ON CONFLICT (code) DO NOTHING;%n",
id, code, escapeSql(fullName), sectorId);
}
}


writer.println();
writer.println("-- Villages");
Map<String, List<String>> villagesByCell = RwandanLocationData.getVillagesByCell();
for (Map.Entry<String, List<String>> entry : villagesByCell.entrySet()) {
String key = entry.getKey();
String[] parts = key.split("-");
if (parts.length < 3)
continue;
String sectorName = parts[1];
String cellName = parts[2].replace("_", "");

String cellCode = generateCode("CELL", cellName + sectorName);
String cellId = getOrCreateId(cellCode);

for (String villageName : entry.getValue()) {
String cleanVillageName = villageName.replace("_", "");
String code = generateCode("VIL", cleanVillageName + cellName + sectorName);
String id = getOrCreateId(code);

writer.printf(
"INSERT INTO location (id, code, name, type, parent_id) VALUES ('%s', '%s', '%s', 'Village', '%s') ON CONFLICT (code) DO NOTHING;%n",
id, code, escapeSql(cleanVillageName), cellId);
}
}

System.out.println("SQL script written to " + OUTPUT_FILE);

} catch (IOException e) {
e.printStackTrace();
}
}

private static String getOrCreateId(String code) {
if (!idCache.containsKey(code)) {
idCache.put(code, UUID.randomUUID().toString());
}
return idCache.get(code);
}

private static String generateCode(String prefix, String name) {
String cleanName = name.toUpperCase().replaceAll("[^A-Z0-9]", "");
return prefix + "-" + cleanName;
}

private static String escapeSql(String val) {
return val.replace("'", "''");
}
}
