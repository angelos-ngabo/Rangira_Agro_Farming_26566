package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location {

@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;

@Column(name = "province", nullable = false, length = 100)
private String province;

@Column(name = "district", nullable = false, length = 100)
private String district;

@Column(name = "sector", nullable = false, length = 100)
private String sector;

@Column(name = "cell", nullable = false, length = 100)
private String cell;

@Column(name = "village", nullable = false, length = 100)
private String village;

@Column(name = "code", nullable = false, length = 100, unique = true)
private String code;

@CreationTimestamp
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;
}

