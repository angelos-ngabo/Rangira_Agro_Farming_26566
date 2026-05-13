package com.raf.repository;

import com.raf.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

Optional<Location> findByCode(String code);

boolean existsByCode(String code);

Optional<Location> findByProvinceAndDistrictAndSectorAndCellAndVillage(
String province, String district, String sector, String cell, String village);

@Query("SELECT DISTINCT l.province FROM Location l ORDER BY l.province")
List<String> findDistinctProvinces();

@Query("SELECT DISTINCT l.district FROM Location l WHERE l.province = :province ORDER BY l.district")
List<String> findDistinctDistrictsByProvince(@Param("province") String province);

@Query("SELECT DISTINCT l.sector FROM Location l WHERE l.province = :province AND l.district = :district ORDER BY l.sector")
List<String> findDistinctSectorsByProvinceAndDistrict(@Param("province") String province, @Param("district") String district);

@Query("SELECT DISTINCT l.cell FROM Location l WHERE l.province = :province AND l.district = :district AND l.sector = :sector ORDER BY l.cell")
List<String> findDistinctCellsByProvinceAndDistrictAndSector(@Param("province") String province, @Param("district") String district, @Param("sector") String sector);

@Query("SELECT l FROM Location l WHERE l.province = :province AND l.district = :district AND l.sector = :sector AND l.cell = :cell ORDER BY l.village")
List<Location> findVillagesByProvinceAndDistrictAndSectorAndCell(@Param("province") String province, @Param("district") String district, @Param("sector") String sector, @Param("cell") String cell);

@Query("SELECT l FROM Location l WHERE LOWER(l.village) LIKE LOWER(CONCAT('%', :name, '%'))")
List<Location> searchByVillageName(@Param("name") String name);
}