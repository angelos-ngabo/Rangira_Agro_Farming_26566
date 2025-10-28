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
 * District Entity
 * Second-level administrative division in Rwanda
 */
@Entity
@Table(name = "district")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class District extends BaseEntity {
    
    @NotBlank(message = "District code is required")
    @Column(name = "district_code", unique = true, nullable = false, length = 10)
    private String districtCode;
    
    @NotBlank(message = "District name is required")
    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;
    
    // Many-to-One relationship with Province
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    @NotNull(message = "Province is required")
    @JsonBackReference
    @ToString.Exclude
    private Province province;
    
    // One-to-Many relationship with Sector
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Sector> sectors = new ArrayList<>();
}

