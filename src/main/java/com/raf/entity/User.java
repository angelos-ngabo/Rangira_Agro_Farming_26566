package com.raf.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends BaseEntity {

@NotBlank(message = "User code is required")
@Column(name = "user_code", unique = true, nullable = false, length = 20)
private String userCode;

@NotBlank(message = "First name is required")
@Column(name = "first_name", nullable = false, length = 50)
private String firstName;

@NotBlank(message = "Last name is required")
@Column(name = "last_name", nullable = false, length = 50)
private String lastName;

@NotBlank(message = "Email is required")
@Email(message = "Invalid email format")
@Column(name = "email", unique = true, nullable = false, length = 100)
private String email;

@NotBlank(message = "Phone number is required")
@Column(name = "phone_number", unique = true, nullable = false, length = 20)
private String phoneNumber;

@NotBlank(message = "Password is required")
@Column(name = "password", nullable = false)
@JsonIgnore
private String password;

@Enumerated(EnumType.STRING)
@Column(name = "user_type", nullable = false, length = 20)
@NotNull(message = "User type is required")
private UserType userType;

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 20)
private UserStatus status = UserStatus.ACTIVE;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "location_id", nullable = false)
@NotNull(message = "Location is required")
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
@ToString.Exclude
private Location location;

@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
@JsonManagedReference("user-profile")
@ToString.Exclude
private UserProfile userProfile;

@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "farmer", "storekeeper", "warehouse", "cropType", "transactions", "enquiries"})
@ToString.Exclude
private List<Inventory> inventoriesAsFarmer = new ArrayList<>();

@OneToMany(mappedBy = "storekeeper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "farmer", "storekeeper", "warehouse", "cropType", "transactions", "enquiries"})
@ToString.Exclude
private List<Inventory> inventoriesAsStorekeeper = new ArrayList<>();

@OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "buyer", "seller", "inventory", "enquiry", "payments", "notifications", "ratings"})
@ToString.Exclude
private List<Transaction> transactionsAsBuyer = new ArrayList<>();

@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "buyer", "seller", "inventory", "enquiry", "payments", "notifications", "ratings"})
@ToString.Exclude
private List<Transaction> transactionsAsSeller = new ArrayList<>();

@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("user-warehouse-accesses")
@ToString.Exclude
private List<WarehouseAccess> warehouseAccesses = new ArrayList<>();


@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
name = "user_crop_interest",
joinColumns = @JoinColumn(name = "user_id"),
inverseJoinColumns = @JoinColumn(name = "crop_type_id")
)
@JsonIgnore
@ToString.Exclude
private List<CropType> interestedCropTypes = new ArrayList<>();

@OneToMany(mappedBy = "rater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("rater-ratings")
@ToString.Exclude
private List<Rating> ratingsGiven = new ArrayList<>();

@OneToMany(mappedBy = "ratedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("rated-user-ratings")
@ToString.Exclude
private List<Rating> ratingsReceived = new ArrayList<>();

@OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private List<Enquiry> enquiriesAsBuyer = new ArrayList<>();

@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private List<Enquiry> enquiriesAsFarmer = new ArrayList<>();

@OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private List<Payment> payments = new ArrayList<>();

@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private List<Notification> notifications = new ArrayList<>();

@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString.Exclude
private Wallet wallet;

@Column(name = "notifications_enabled", nullable = false)
private Boolean notificationsEnabled = true;

@Column(name = "two_factor_enabled", nullable = false)
private Boolean twoFactorEnabled = false;
}

