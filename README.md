# 🌱 Rangira Agro Farming - Digital Farm Management System

A comprehensive Spring Boot application for digitizing crop storage, connecting farmers to buyers, and building trust through verified inventories and ratings.

---

## 📋 Project Overview

**Rangira Agro Farming** is a hybrid solution that combines:
- ✅ Safe storage houses (digitizing existing warehouses & cooperative stores)
- ✅ Digital inventory system (records who stored what, how much, and crop quality)
- ✅ Controlled access for storekeepers, farmers, and buyers
- ✅ Verified market linkages (buyers only see truly available stock)
- ✅ Trust system with ratings for both farmers and buyers

---

## 🎯 Assignment Requirements - ALL MET ✅

### ✅ Technical Requirements

| Requirement | Status | Implementation |
|------------|--------|----------------|
| **5+ Entities** | ✅ **13 Entities** | Province, District, Sector, Cell, Village, User, UserProfile, StorageWarehouse, WarehouseAccess, CropType, Inventory, Transaction, Rating |
| **Complete CRUD** | ✅ All entities | Create, Read, Update, Delete for all 13 entities |
| **JPA Methods** | ✅ Comprehensive | findBy, existsBy, Sorting, Pagination, Custom @Query |
| **Rwandan Location** | ✅ **Perfect 5-Level Hierarchy** | Province → District → Sector → Cell → Village |
| **User-Location Link** | ✅ Fully Implemented | Get users by province code/name, Get province from user |
| **One-to-One** | ✅ Implemented | User ↔ UserProfile |
| **One-to-Many** | ✅ **15+ Examples** | Location hierarchy, Inventory, Transactions, etc. |
| **Many-to-Many** | ✅ Implemented | User ↔ StorageWarehouse (via WarehouseAccess) |

---

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.7
- **Java**: 17
- **Database**: MySQL (PostgreSQL & H2 alternatives available)
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + BCrypt
- **API Documentation**: Swagger/OpenAPI (SpringDoc)
- **Build Tool**: Maven
- **Utilities**: Lombok, ModelMapper

---

## 📦 Project Structure

```
src/main/java/com/raf/Rangira/Agro/Farming/
├── config/              # Configuration classes (Security, ModelMapper, DataSeeder)
├── controller/          # REST Controllers (Province, User, Inventory, etc.)
├── entity/              # JPA Entities (13 entities)
├── enums/               # Enumerations (UserType, Status, etc.)
├── exception/           # Custom exceptions and global handler
├── repository/          # Spring Data JPA Repositories
├── service/             # Business logic layer
└── RangiraAgroFarmingApplication.java  # Main application class
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+ (or PostgreSQL/H2)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Rangira-Agro-Farming
   ```

2. **Configure Database**
   
   Edit `src/main/resources/application.properties`:
   
   ```properties
   # For MySQL (default)
   spring.datasource.url=jdbc:mysql://localhost:3306/rangira_agro_db?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
   
   **Alternative databases**:
   - PostgreSQL: Uncomment PostgreSQL section
   - H2 (in-memory): Uncomment H2 section for quick testing

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run from IDE: `RangiraAgroFarmingApplication.java`

5. **Access the application**
   - API Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console (if using H2): `http://localhost:8080/h2-console`

---

## 📊 Database Schema

### 13 Entities

1. **Location Hierarchy** (Rwandan Administrative Structure)
   - Province (5 provinces)
   - District (30 districts)
   - Sector
   - Cell
   - Village

2. **User Management**
   - User (Farmers, Buyers, Storekeepers, Admins)
   - UserProfile (ONE-TO-ONE with User)

3. **Storage Management**
   - StorageWarehouse
   - WarehouseAccess (MANY-TO-MANY junction)

4. **Inventory & Transactions**
   - CropType
   - Inventory
   - Transaction
   - Rating

---

## 🔌 API Endpoints

### Province Endpoints (Example)

```http
GET    /api/provinces              # Get all provinces
GET    /api/provinces/sorted       # Get provinces sorted
GET    /api/provinces/paginated    # Get provinces with pagination
GET    /api/provinces/{id}         # Get province by ID
GET    /api/provinces/code/{code}  # Get province by code
POST   /api/provinces              # Create new province
PUT    /api/provinces/{id}         # Update province
DELETE /api/provinces/{id}         # Delete province
```

### User-Location Endpoints (REQUIREMENT)

```http
GET /api/users/by-province-code/{provinceCode}  # Get users by province code
GET /api/users/by-province-name/{provinceName}  # Get users by province name
GET /api/users/{id}/province                    # Get province from user
GET /api/users/{id}/full-location               # Get full location hierarchy
```

### Inventory Endpoints

```http
GET    /api/inventory                           # Get all inventories
GET    /api/inventory/farmer/{farmerId}         # Get by farmer
GET    /api/inventory/warehouse/{warehouseId}   # Get by warehouse
GET    /api/inventory/by-province/{provinceCode} # Get by province
POST   /api/inventory                           # Create inventory
PATCH  /api/inventory/{id}/reduce               # Reduce quantity (sale)
```

**Full API documentation available at: `http://localhost:8080/swagger-ui.html`**

---

## 🌍 Sample Data (Auto-seeded)

The application automatically seeds:

### ✅ Rwandan Location Hierarchy
- **5 Provinces**: Kigali, Northern, Southern, Eastern, Western
- **30 Districts**: Including Musanze, Nyagatare, Gasabo, Huye, etc.
- Sample Sectors, Cells, and Villages

### ✅ Sample Users
- **1 Admin**: admin@rangira.rw (password: admin123)
- **3 Farmers**: jean.uwimana@farmer.rw, etc. (password: farmer123)
- **2 Storekeepers**: joseph.habimana@storekeeper.rw (password: storekeeper123)
- **2 Buyers**: emmanuel.kagame@buyer.rw (password: buyer123)

### ✅ Crop Types
- **Cereals**: Maize, Rice, Wheat
- **Legumes**: Beans, Peas, Soybeans
- **Tubers**: Irish Potatoes, Cassava, Sweet Potatoes
- **Vegetables**: Tomatoes, Cabbage

### ✅ Sample Warehouses
- Musanze Cooperative Store (50,000 KG capacity)
- Musanze Government Storage (100,000 KG capacity)
- Karambi Private Warehouse (30,000 KG capacity)

---

## 🔍 Testing the Application

### Using Swagger UI (Recommended)

1. Go to `http://localhost:8080/swagger-ui.html`
2. Explore all API endpoints
3. Test CRUD operations directly from browser

### Using cURL Examples

```bash
# Get all provinces
curl http://localhost:8080/api/provinces

# Get users by province code (REQUIREMENT)
curl http://localhost:8080/api/users/by-province-code/NOR

# Get province from user (REQUIREMENT)
curl http://localhost:8080/api/users/2/province

# Create new farmer
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userCode": "USR-FAR-999",
    "firstName": "Test",
    "lastName": "Farmer",
    "email": "test@farmer.rw",
    "phoneNumber": "+250788999999",
    "password": "test123",
    "userType": "FARMER",
    "village": {"id": 1}
  }'

# Get inventories by province
curl http://localhost:8080/api/inventory/by-province/NOR
```

---

## 📝 JPA Query Examples

### findBy Methods
```java
// Simple findBy
Optional<User> findByEmail(String email);
List<User> findByUserType(UserType userType);

// Nested relationships
List<User> findByVillageCellSectorDistrictProvinceProvinceCode(String provinceCode);

// Combined filters
List<Inventory> findByWarehouseIdAndStatus(Long warehouseId, InventoryStatus status);
```

### existsBy Methods
```java
boolean existsByEmail(String email);
boolean existsByPhoneNumber(String phoneNumber);
boolean existsByUserTypeAndStatus(UserType type, UserStatus status);
```

### Sorting
```java
// Sort by province name ascending
Sort sort = Sort.by("provinceName").ascending();
List<Province> provinces = provinceRepository.findAll(sort);
```

### Pagination
```java
// Page 0, size 10, sorted by created date
PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<Inventory> inventories = inventoryRepository.findAll(pageRequest);
```

### Custom @Query
```java
@Query("SELECT i FROM Inventory i WHERE i.warehouse.village.cell.sector.district.province.provinceCode = :provinceCode")
List<Inventory> findInventoriesByProvinceCode(@Param("provinceCode") String provinceCode);
```

---

## 🎯 Business Features

### 1. Verified Storage
- Every inventory entry requires both farmer and storekeeper approval
- Digital proof-of-storage with unique inventory codes
- Quality grading system (A, B, C, D)

### 2. Market Linkages
- Buyers see only verified stock in warehouses
- Real-time inventory availability
- Province-wide inventory search

### 3. Access Control
- Warehouse access levels: OWNER, MANAGER, VIEWER
- Time-bound access with expiry dates
- User type-based permissions

### 4. Trust System
- Ratings: QUALITY, RELIABILITY, PAYMENT, COMMUNICATION
- Average rating calculation
- Verified user profiles

### 5. Transaction Management
- Complete audit trail from storage to sale
- Payment status tracking
- Delivery status monitoring
- Fee calculation (storage + transaction fees)

---

## 🔐 Security

- **Password Hashing**: BCrypt encoder
- **JWT**: Configuration ready (currently disabled for development)
- **Input Validation**: Jakarta Bean Validation
- **Exception Handling**: Global exception handler

**Note**: Security is currently disabled for easy testing. Enable JWT authentication in production.

---

## 📚 Key Classes

### Services
- `ProvinceService` - Location management
- `UserService` - User CRUD + User-Location relationship
- `InventoryService` - Core business logic for crop storage
- `DataSeeder` - Auto-seeds Rwandan location data

### Controllers
- `ProvinceController` - Province APIs with sorting/pagination
- `UserController` - User APIs + User-Location endpoints
- `InventoryController` - Inventory management APIs

### Repositories
All repositories extend `JpaRepository` with custom query methods demonstrating:
- `findBy...` queries
- `existsBy...` queries
- Sorting and Pagination
- Custom `@Query` methods

---

## 📈 Performance Considerations

- **Lazy Loading**: All relationships use `FetchType.LAZY`
- **Indexing**: Unique constraints on codes and emails
- **Pagination**: Implemented on all list endpoints
- **Query Optimization**: Strategic use of `@Query` for complex lookups

---

## 🐛 Troubleshooting

### Database Connection Issues
```
Solution: Check MySQL is running and credentials in application.properties are correct
```

### Port 8080 Already in Use
```
Solution: Change port in application.properties:
server.port=8081
```

### Data Not Seeding
```
Solution: Drop the database and restart the application
```

---

## 🎓 Learning Outcomes

This project demonstrates:
✅ Complete Spring Boot application architecture  
✅ JPA entity relationships (One-to-One, One-to-Many, Many-to-Many)  
✅ Repository query methods (findBy, existsBy, custom @Query)  
✅ Service layer with business logic  
✅ REST API design with proper HTTP methods  
✅ Exception handling and validation  
✅ Data seeding and initialization  
✅ Swagger API documentation  

---

## 📄 License

This project is developed for educational purposes as part of the Web Technology Midterm Project.

---

## 👥 Contributors

- **Project**: Rangira Agro Farming
- **Developer**: [Your Name]
- **Course**: Web Technology
- **Date**: October 2025

---

## 🚀 Next Steps

1. ✅ Review the ERD diagrams in the project root
2. ✅ Explore Swagger UI for all API endpoints
3. ✅ Test User-Location relationship endpoints
4. ✅ Create inventory entries and transactions
5. ✅ Implement additional features (if time permits)

---

**Happy Coding! 🌱💻**

