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
 * Village Entity
 * Lowest administrative division in Rwanda (Fifth level)
 */
@Entity
@Table(name = "village")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Village extends BaseEntity {
    
    @NotBlank(message = "Village code is required")
    @Column(name = "village_code", unique = true, nullable = false, length = 10)
    private String villageCode;
    
    @NotBlank(message = "Village name is required")
    @Column(name = "village_name", nullable = false, length = 100)
    private String villageName;
    
    // Many-to-One relationship with Cell
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cell_id", nullable = false)
    @NotNull(message = "Cell is required")
    @JsonBackReference
    @ToString.Exclude
    private Cell cell;
    
    // One-to-Many relationship with User
    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<User> users = new ArrayList<>();
    
    // One-to-Many relationship with StorageWarehouse
    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<StorageWarehouse> warehouses = new ArrayList<>();
}

