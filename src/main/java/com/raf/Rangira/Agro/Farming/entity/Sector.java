package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Sector Entity
 * Third-level administrative division in Rwanda
 */
@Entity
@Table(name = "sector")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sector extends BaseEntity {
    
    @NotBlank(message = "Sector code is required")
    @Column(name = "sector_code", unique = true, nullable = false, length = 10)
    private String sectorCode;
    
    @NotBlank(message = "Sector name is required")
    @Column(name = "sector_name", nullable = false, length = 100)
    private String sectorName;
    
    // Many-to-One relationship with District
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    @NotNull(message = "District is required")
    @JsonBackReference
    @ToString.Exclude
    private District district;
    
    // One-to-Many relationship with Cell
    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Cell> cells = new ArrayList<>();
}

