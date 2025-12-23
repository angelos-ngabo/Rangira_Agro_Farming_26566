package com.raf.repository;

import com.raf.entity.InventoryRequest;
import com.raf.enums.InventoryRequestStatus;
import com.raf.enums.InventoryRequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {

Optional<InventoryRequest> findByRequestCode(String requestCode);

List<InventoryRequest> findByFarmerId(Long farmerId);

List<InventoryRequest> findByStorekeeperId(Long storekeeperId);

List<InventoryRequest> findByInventoryId(Long inventoryId);

List<InventoryRequest> findByStatus(InventoryRequestStatus status);

List<InventoryRequest> findByRequestType(InventoryRequestType requestType);

List<InventoryRequest> findByStorekeeperIdAndStatus(Long storekeeperId, InventoryRequestStatus status);

List<InventoryRequest> findByFarmerIdAndStatus(Long farmerId, InventoryRequestStatus status);

@Query("SELECT ir FROM InventoryRequest ir WHERE ir.storekeeper.id = :storekeeperId AND ir.inventory.warehouse.id = :warehouseId")
List<InventoryRequest> findByStorekeeperIdAndWarehouseId(@Param("storekeeperId") Long storekeeperId, @Param("warehouseId") Long warehouseId);
}

