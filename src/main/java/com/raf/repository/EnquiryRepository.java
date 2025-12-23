package com.raf.repository;

import com.raf.entity.Enquiry;
import com.raf.enums.EnquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

Optional<Enquiry> findByEnquiryCode(String enquiryCode);

@Query("SELECT e FROM Enquiry e LEFT JOIN FETCH e.transaction WHERE e.buyer.id = :buyerId")
List<Enquiry> findByBuyerId(@Param("buyerId") Long buyerId);

@Query("SELECT e FROM Enquiry e LEFT JOIN FETCH e.transaction WHERE e.farmer.id = :farmerId")
List<Enquiry> findByFarmerId(@Param("farmerId") Long farmerId);

List<Enquiry> findByInventoryId(Long inventoryId);

List<Enquiry> findByStatus(EnquiryStatus status);

@Query("SELECT e FROM Enquiry e LEFT JOIN FETCH e.transaction WHERE e.buyer.id = :buyerId AND e.status = :status")
List<Enquiry> findByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") EnquiryStatus status);

@Query("SELECT e FROM Enquiry e LEFT JOIN FETCH e.transaction WHERE e.farmer.id = :farmerId AND e.status = :status")
List<Enquiry> findByFarmerIdAndStatus(@Param("farmerId") Long farmerId, @Param("status") EnquiryStatus status);

boolean existsByEnquiryCode(String enquiryCode);

@Query("SELECT e FROM Enquiry e LEFT JOIN FETCH e.transaction WHERE e.buyer.id = :buyerId OR e.farmer.id = :farmerId ORDER BY e.enquiryDate DESC")
List<Enquiry> findEnquiriesForUser(@Param("buyerId") Long buyerId, @Param("farmerId") Long farmerId);
}

