# Rangira Agro Farming

A Spring Boot application for farm management and crop storage.

## Features

- User management (Farmers, Buyers, Storekeepers, Admins)
- Warehouse management
- Crop inventory tracking
- Transaction recording
- Rating system
- Location hierarchy (Rwanda)

## Project Structure

### Entities (9 total)

1. **Location** - Self-referential table for Province, District, Sector, Cell, Village
2. **User** - System users
3. **UserProfile** - User details (One-to-One with User)
4. **StorageWarehouse** - Warehouse information
5. **WarehouseAccess** - User access to warehouses (Many-to-Many junction)
6. **CropType** - Types of crops
7. **Inventory** - Crop storage records
8. **Transaction** - Buy/sell transactions
9. **Rating** - User ratings

### Relationships

- **One-to-One**: User ↔ UserProfile
- **One-to-Many**: Location → Users, Location → Warehouses, User → Inventories, etc.
- **Many-to-Many**: User ↔ Warehouse (via WarehouseAccess)

### Location Structure

The `Location` table uses a self-referential design:
- Each location has a `level` (PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE)
- Each location has a `parent_id` pointing to its parent location
- Provinces have `parent_id = NULL`

Example:
```
Kigali (Province, parent=null)
  └─ Gasabo (District, parent=Kigali)
      └─ Kimironko (Sector, parent=Gasabo)
          └─ Kibagabaga (Cell, parent=Kimironko)
              └─ Kibagabaga Village (Village, parent=Kibagabaga)
```

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.5.7
- **Database**: PostgreSQL (configurable to MySQL/H2)
- **ORM**: JPA/Hibernate
- **Security**: BCrypt password encoding
- **API Docs**: Swagger/OpenAPI

## Setup

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL (or MySQL/H2)

### Database Configuration

Edit `src/main/resources/application.properties`:



### Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### Access Swagger UI

Open your browser: `http://localhost:8080/swagger-ui.html`

## Sample Data

On first run, the application seeds:

### Locations
- 5 Provinces (Kigali, Northern, Southern, Eastern, Western)
- Sample Districts, Sectors, Cells, and Villages

### Users
- Admin: admin@rangira.rw (password: admin123)
- Farmers, Buyers, Storekeepers (password: farmer123, buyer123, storekeeper123)

### Warehouses
- 3 sample warehouses in different locations

### Crop Types
- Maize, Beans, Rice, Wheat, Potatoes, Cassava, Tomatoes, Cabbage

## 🗂️ **Database ERD**

### **View the Entity Relationship Diagram:**
- 📊 **[ERD_DOCUMENTATION.md](ERD_DOCUMENTATION.md)** - Complete ERD with PlantUML code
- 🎨 **[ERD_VISUAL.md](ERD_VISUAL.md)** - ASCII art visual diagram
- 💾 **[generate-erd.sql](generate-erd.sql)** - SQL script to generate ERD

### **Database Overview:**
- **9 Tables** with proper relationships
- **Hierarchical Location** (self-referencing for Rwanda's admin divisions)
- **Multi-role Users** (Farmer, Buyer, Storekeeper, Admin)
- **Many-to-Many** Warehouse Access with junction table
- **Complete Transaction Tracking** with Ratings system

---

## 🧪 Testing

### 📘 **COMPLETE TESTING GUIDE**

For a comprehensive, step-by-step testing workflow, see **[TESTING_GUIDE.md](TESTING_GUIDE.md)**

This guide includes:
- ✅ Complete dependency chain explanation
- ✅ Step-by-step POST/GET requests in correct order
- ✅ Sample data for all entities (Users, Inventories, Transactions, Ratings, etc.)
- ✅ Advanced query examples (pagination, search, filters)
- ✅ Update and Delete operations
- ✅ Common errors and solutions

### 🔧 **UPDATE OPERATIONS GUIDE**

**Having trouble with PUT/UPDATE?** See **[UPDATE_OPERATIONS_GUIDE.md](UPDATE_OPERATIONS_GUIDE.md)**

This guide covers:
- ✅ How to fix "Location is required" and similar errors
- ✅ Creating and updating Crop Types
- ✅ Updating Warehouses with the new DTO
- ✅ PUT vs PATCH - when to use each
- ✅ Complete working examples for all entities

### 📦 **POSTMAN COLLECTION**

Import the Postman collection for easy testing:
- **File**: `Rangira_Agro_Farming_API.postman_collection.json`
- **Import**: Open Postman → Import → Select the JSON file
- **Base URL**: Already configured as `http://localhost:8080`

### 🎯 **CRITICAL: Testing Order**

**⚠️ Follow this order to avoid dependency errors:**

```
1. ✅ Locations (seeded automatically)
2. ✅ Crop Types (seeded automatically)
3. ✅ Users (5 users seeded)
4. ✅ Warehouses (3 warehouses seeded)
5. ⚠️  Warehouse Access (create access records)
6. ⚠️  Inventories (MUST create BEFORE transactions)
7. ⚠️  Transactions (MUST create BEFORE ratings)
8. ⚠️  Ratings (requires existing transactions)
```

**Why this order?**
- Ratings depend on Transactions
- Transactions depend on Inventories
- Inventories depend on Users, Warehouses, and Crop Types

## API Endpoints

### Locations
- `GET /api/locations` - All locations
- `GET /api/locations/provinces` - Get all provinces
- `GET /api/locations/level/{level}` - Get by level (PROVINCE, DISTRICT, etc.)
- `GET /api/locations/code/{code}` - Get by code
- `POST /api/locations` - Create location

### Users
- `GET /api/users` - All users
- `GET /api/users/{id}` - Get by ID
- `GET /api/users/location/code/{code}` - Get users by location code
- `GET /api/users/{id}/location` - Get user's location
- `POST /api/users` - Create user (use UserRequest DTO with locationId)

### Warehouses
- `GET /api/warehouses` - All warehouses
- `POST /api/warehouses` - Create warehouse

### Warehouse Access
- `GET /api/warehouse-access` - All access records
- `GET /api/warehouse-access/user/{userId}` - Access by user
- `POST /api/warehouse-access` - Grant access (use WarehouseAccessRequest DTO)

### Inventories ⚠️ Create BEFORE transactions
- `GET /api/inventories` - All inventories
- `GET /api/inventories/farmer/{farmerId}` - By farmer
- `GET /api/inventories/warehouse/{warehouseId}` - By warehouse
- `POST /api/inventories` - Create inventory (use InventoryRequest DTO with IDs)

### Transactions ⚠️ Requires existing inventories
- `GET /api/transactions` - All transactions
- `GET /api/transactions/buyer/{buyerId}` - By buyer
- `GET /api/transactions/seller/{sellerId}` - By seller
- `POST /api/transactions` - Create transaction (use TransactionRequest DTO with IDs)

### Ratings ⚠️ Requires existing transactions
- `GET /api/ratings` - All ratings
- `GET /api/ratings/rater/{raterId}` - By rater
- `GET /api/ratings/rated-user/{ratedUserId}` - By rated user
- `POST /api/ratings` - Create rating (use RatingRequest DTO with IDs)

## JPA Features Demonstrated

### findBy Methods
```java
findByLocationCode(String code)
findByUserType(UserType type)
findByUserTypeAndStatus(UserType type, UserStatus status)
```

### existsBy Methods
```java
existsByEmail(String email)
existsByPhoneNumber(String phone)
existsByUserTypeAndStatus(UserType type, UserStatus status)
```

### Sorting
```java
Sort.by("name").ascending()
List<Location> locations = locationRepository.findByLevel(LocationLevel.PROVINCE, sort);
```

### Pagination
```java
PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
Page<User> users = userRepository.findAll(pageRequest);
```

### Custom Queries
```java
@Query("SELECT u FROM User u WHERE u.location.code = :locationCode")
List<User> findByLocationCode(@Param("locationCode") String locationCode);
```

## Notes

- Database is recreated on each run (`spring.jpa.hibernate.ddl-auto=create`)
- Change to `update` in production to preserve data
- All passwords are BCrypt encrypted
- Swagger UI available for testing all endpoints

## Project Author

Ngabo Angelos
Web Technology Midterm Project
October 2025
