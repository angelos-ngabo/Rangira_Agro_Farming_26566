package com.raf.Rangira.Agro.Farming.service;

import com.raf.Rangira.Agro.Farming.entity.WarehouseAccess;
import com.raf.Rangira.Agro.Farming.enums.AccessLevel;
import com.raf.Rangira.Agro.Farming.exception.ResourceNotFoundException;
import com.raf.Rangira.Agro.Farming.repository.WarehouseAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * WarehouseAccess Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseAccessService {
    
    private final WarehouseAccessRepository warehouseAccessRepository;
    
    /**
     * Create a new warehouse access
     */
    public WarehouseAccess createWarehouseAccess(WarehouseAccess warehouseAccess) {
        log.info("Creating new warehouse access for user ID: {} and warehouse ID: {}", 
            warehouseAccess.getUser().getId(), warehouseAccess.getWarehouse().getId());
        return warehouseAccessRepository.save(warehouseAccess);
    }
    
    /**
     * Get all warehouse accesses
     */
    @Transactional(readOnly = true)
    public List<WarehouseAccess> getAllWarehouseAccesses() {
        return warehouseAccessRepository.findAll();
    }
    
    /**
     * Get warehouse accesses with pagination
     */
    @Transactional(readOnly = true)
    public Page<WarehouseAccess> getWarehouseAccessesPaginated(Pageable pageable) {
        return warehouseAccessRepository.findAll(pageable);
    }
    
    /**
     * Get warehouse access by ID
     */
    @Transactional(readOnly = true)
    public WarehouseAccess getWarehouseAccessById(Long id) {
        return warehouseAccessRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse access not found with ID: " + id));
    }
    
    /**
     * Get warehouse accesses by user
     */
    @Transactional(readOnly = true)
    public List<WarehouseAccess> getWarehouseAccessesByUser(Long userId) {
        return warehouseAccessRepository.findByUserId(userId);
    }
    
    /**
     * Get warehouse accesses by warehouse
     */
    @Transactional(readOnly = true)
    public List<WarehouseAccess> getWarehouseAccessesByWarehouse(Long warehouseId) {
        return warehouseAccessRepository.findByWarehouseId(warehouseId);
    }
    
    /**
     * Get warehouse access for specific user and warehouse
     */
    @Transactional(readOnly = true)
    public WarehouseAccess getWarehouseAccessByUserAndWarehouse(Long userId, Long warehouseId) {
        return warehouseAccessRepository.findByUserIdAndWarehouseId(userId, warehouseId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Warehouse access not found for user ID: " + userId + " and warehouse ID: " + warehouseId));
    }
    
    /**
     * Get warehouse accesses by access level
     */
    @Transactional(readOnly = true)
    public List<WarehouseAccess> getWarehouseAccessesByAccessLevel(AccessLevel accessLevel) {
        return warehouseAccessRepository.findByAccessLevel(accessLevel);
    }
    
    /**
     * Get active warehouse accesses
     */
    @Transactional(readOnly = true)
    public List<WarehouseAccess> getActiveWarehouseAccesses() {
        LocalDate today = LocalDate.now();
        return warehouseAccessRepository.findAll().stream()
            .filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
            .toList();
    }
    
    /**
     * Get active warehouse accesses for user
     */
    @Transactional(readOnly = true)
    public List<WarehouseAccess> getActiveWarehouseAccessesForUser(Long userId) {
        LocalDate today = LocalDate.now();
        return getWarehouseAccessesByUser(userId).stream()
            .filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
            .toList();
    }
    
    /**
     * Update warehouse access
     */
    public WarehouseAccess updateWarehouseAccess(Long id, WarehouseAccess warehouseAccessDetails) {
        WarehouseAccess warehouseAccess = getWarehouseAccessById(id);
        
        warehouseAccess.setAccessLevel(warehouseAccessDetails.getAccessLevel());
        warehouseAccess.setGrantedDate(warehouseAccessDetails.getGrantedDate());
        warehouseAccess.setExpiryDate(warehouseAccessDetails.getExpiryDate());
        
        log.info("Updating warehouse access ID: {}", id);
        return warehouseAccessRepository.save(warehouseAccess);
    }
    
    /**
     * Update access level
     */
    public WarehouseAccess updateAccessLevel(Long id, AccessLevel accessLevel) {
        WarehouseAccess warehouseAccess = getWarehouseAccessById(id);
        warehouseAccess.setAccessLevel(accessLevel);
        
        log.info("Updating warehouse access ID {} access level to: {}", id, accessLevel);
        return warehouseAccessRepository.save(warehouseAccess);
    }
    
    /**
     * Revoke warehouse access (set expiry date to today)
     */
    public WarehouseAccess revokeWarehouseAccess(Long id) {
        WarehouseAccess warehouseAccess = getWarehouseAccessById(id);
        warehouseAccess.setExpiryDate(LocalDate.now());
        
        log.info("Revoking warehouse access ID: {}", id);
        return warehouseAccessRepository.save(warehouseAccess);
    }
    
    /**
     * Delete warehouse access
     */
    public void deleteWarehouseAccess(Long id) {
        WarehouseAccess warehouseAccess = getWarehouseAccessById(id);
        log.info("Deleting warehouse access ID: {}", id);
        warehouseAccessRepository.delete(warehouseAccess);
    }
    
    /**
     * Check if user has access to warehouse
     */
    @Transactional(readOnly = true)
    public boolean hasAccess(Long userId, Long warehouseId) {
        return warehouseAccessRepository.existsByUserIdAndWarehouseId(userId, warehouseId);
    }
    
    /**
     * Get total number of warehouse accesses
     */
    @Transactional(readOnly = true)
    public long getTotalWarehouseAccesses() {
        return warehouseAccessRepository.count();
    }
    
    /**
     * Count active warehouse accesses for warehouse
     */
    @Transactional(readOnly = true)
    public long countActiveAccessesForWarehouse(Long warehouseId) {
        LocalDate today = LocalDate.now();
        return getWarehouseAccessesByWarehouse(warehouseId).stream()
            .filter(wa -> wa.getExpiryDate() == null || wa.getExpiryDate().isAfter(today))
            .count();
    }
}

