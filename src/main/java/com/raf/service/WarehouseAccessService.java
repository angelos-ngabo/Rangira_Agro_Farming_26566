package com.raf.service;

import com.raf.dto.WarehouseAccessRequest;
import com.raf.entity.StorageWarehouse;
import com.raf.entity.User;
import com.raf.entity.WarehouseAccess;
import com.raf.enums.AccessLevel;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.StorageWarehouseRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseAccessService {
    private final WarehouseAccessRepository warehouseAccessRepository;
    private final UserRepository userRepository;
    private final StorageWarehouseRepository warehouseRepository;

    public WarehouseAccess createWarehouseAccessFromRequest(WarehouseAccessRequest request) {
        log.info("Creating warehouse access from request for user ID: {} and warehouse ID: {}", 
            request.getUserId(), request.getWarehouseId());
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
        
        StorageWarehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));
        
        WarehouseAccess warehouseAccess = new WarehouseAccess();
        warehouseAccess.setUser(user);
        warehouseAccess.setWarehouse(warehouse);
        warehouseAccess.setAccessLevel(request.getAccessLevel());
        warehouseAccess.setGrantedDate(request.getGrantedDate());
        warehouseAccess.setExpiryDate(request.getExpiryDate());
        warehouseAccess.setIsActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE);
        
        return warehouseAccessRepository.save(warehouseAccess);
    }

    public WarehouseAccess createWarehouseAccess(WarehouseAccess warehouseAccess) {
        log.info("Creating new warehouse access for user ID: {} and warehouse ID: {}", 
            warehouseAccess.getUser().getId(), warehouseAccess.getWarehouse().getId());
        return warehouseAccessRepository.save(warehouseAccess);
    }

    @Transactional(readOnly = true)
    public List<WarehouseAccess> getAllWarehouseAccesses() {
        return warehouseAccessRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<WarehouseAccess> getWarehouseAccessesPaginated(Pageable pageable) {
        return warehouseAccessRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public WarehouseAccess getWarehouseAccessById(Long id) {
        return warehouseAccessRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<WarehouseAccess> getWarehouseAccessesByUser(Long userId) {
        return warehouseAccessRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<WarehouseAccess> getWarehouseAccessesByWarehouse(Long warehouseId) {
        return warehouseAccessRepository.findByWarehouseId(warehouseId);
    }

    @Transactional(readOnly = true)
    public WarehouseAccess getWarehouseAccessByUserAndWarehouse(Long userId, Long warehouseId) {
        return warehouseAccessRepository.findByUserIdAndWarehouseId(userId, warehouseId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Warehouse access not found for user ID: " + userId + " and warehouse ID: " + warehouseId));
    }

    @Transactional(readOnly = true)
    public List<WarehouseAccess> getWarehouseAccessesByAccessLevel(AccessLevel accessLevel) {
        return warehouseAccessRepository.findByAccessLevel(accessLevel);
    }

    @Transactional(readOnly = true)
    public List<WarehouseAccess> getActiveWarehouseAccesses() {
        LocalDate today = LocalDate.now();
        return warehouseAccessRepository.findAll().stream()
            .filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<WarehouseAccess> getActiveWarehouseAccessesForUser(Long userId) {
        LocalDate today = LocalDate.now();
        return warehouseAccessRepository.findByUserId(userId).stream()
            .filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
            .toList();
    }

    public WarehouseAccess updateWarehouseAccess(Long id, WarehouseAccess warehouseAccessDetails) {
        WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));

        warehouseAccess.setAccessLevel(warehouseAccessDetails.getAccessLevel());
        warehouseAccess.setGrantedDate(warehouseAccessDetails.getGrantedDate());
        warehouseAccess.setExpiryDate(warehouseAccessDetails.getExpiryDate());

        log.info("Updating warehouse access ID: {}", id);
        return warehouseAccessRepository.save(warehouseAccess);
    }

    public WarehouseAccess updateAccessLevel(Long id, AccessLevel accessLevel) {
        WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
        warehouseAccess.setAccessLevel(accessLevel);

        log.info("Updating warehouse access ID {} access level to: {}", id, accessLevel);
        return warehouseAccessRepository.save(warehouseAccess);
    }

    public WarehouseAccess revokeWarehouseAccess(Long id) {
        WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
        warehouseAccess.setExpiryDate(LocalDate.now());

        log.info("Revoking warehouse access ID: {}", id);
        return warehouseAccessRepository.save(warehouseAccess);
    }

    public void deleteWarehouseAccess(Long id) {
        WarehouseAccess warehouseAccess = warehouseAccessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
        log.info("Deleting warehouse access ID: {}", id);
        warehouseAccessRepository.delete(warehouseAccess);
    }

    @Transactional(readOnly = true)
    public boolean hasAccess(Long userId, Long warehouseId) {
        return warehouseAccessRepository.existsByUserIdAndWarehouseId(userId, warehouseId);
    }

    @Transactional(readOnly = true)
    public long getTotalWarehouseAccesses() {
        return warehouseAccessRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveAccessesForWarehouse(Long warehouseId) {
        LocalDate today = LocalDate.now();
        return warehouseAccessRepository.findByWarehouseId(warehouseId).stream()
            .filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
            .count();
    }
}

