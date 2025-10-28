package com.raf.Rangira.Agro.Farming.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Province Entity
 * Top-level administrative division in Rwanda
 */
@Entity
@Table(name = "province")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Province extends BaseEntity {
    
    @NotBlank(message = "Province code is required")
    @Column(name = "province_code", unique = true, nullable = false, length = 10)
    private String provinceCode;
    
    @NotBlank(message = "Province name is required")
    @Column(name = "province_name", unique = true, nullable = false, length = 100)
    private String provinceName;
    
    // One-to-Many relationship with District
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<District> districts = new ArrayList<>();
}

