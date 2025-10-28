package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.raf.Rangira.Agro.Farming.enums.CropCategory;
import com.raf.Rangira.Agro.Farming.enums.MeasurementUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * CropType Entity
 * Master data for different types of crops
 */
@Entity
@Table(name = "crop_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CropType extends BaseEntity {
    
    @NotBlank(message = "Crop code is required")
    @Column(name = "crop_code", unique = true, nullable = false, length = 20)
    private String cropCode;
    
    @NotBlank(message = "Crop name is required")
    @Column(name = "crop_name", nullable = false, length = 100)
    private String cropName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    @NotNull(message = "Category is required")
    private CropCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_unit", nullable = false, length = 20)
    @NotNull(message = "Measurement unit is required")
    private MeasurementUnit measurementUnit;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    // One-to-Many relationship with Inventory
    @OneToMany(mappedBy = "cropType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Inventory> inventories = new ArrayList<>();
}

