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
 * Cell Entity
 * Fourth-level administrative division in Rwanda
 */
@Entity
@Table(name = "cell")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cell extends BaseEntity {
    
    @NotBlank(message = "Cell code is required")
    @Column(name = "cell_code", unique = true, nullable = false, length = 10)
    private String cellCode;
    
    @NotBlank(message = "Cell name is required")
    @Column(name = "cell_name", nullable = false, length = 100)
    private String cellName;
    
    // Many-to-One relationship with Sector
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    @NotNull(message = "Sector is required")
    @JsonBackReference
    @ToString.Exclude
    private Sector sector;
    
    // One-to-Many relationship with Village
    @OneToMany(mappedBy = "cell", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Village> villages = new ArrayList<>();
}

