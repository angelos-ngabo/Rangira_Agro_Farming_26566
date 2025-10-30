package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.LocationLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LocationLevel level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Location parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Location> children = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<StorageWarehouse> warehouses = new ArrayList<>();

    public String getFullHierarchy() {
        StringBuilder hierarchy = new StringBuilder(this.name);
        Location current = this.parent;
        while (current != null) {
            hierarchy.insert(0, current.getName() + " > ");
            current = current.getParent();
        }
        return hierarchy.toString();
    }
}

