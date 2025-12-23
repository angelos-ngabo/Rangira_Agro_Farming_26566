package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raf.enums.ELocation;
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

@Column(name = "name", nullable = false, length = 100)
private String name;

@Column(name = "code", nullable = false, unique = true, length = 50)
private String code;

@Enumerated(EnumType.STRING)
@Column(name = "type", nullable = false, length = 20)
private ELocation type;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_id")
@JsonIgnore
private Location parent;

@CreationTimestamp
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;
}

