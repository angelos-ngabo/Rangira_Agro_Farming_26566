# 📘 Rangira Agro Farming - Complete API Documentation

## Base URL
```
http://localhost:8080/api
```

## Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 🌍 Province APIs

### Get All Provinces
```http
GET /api/provinces
```

**Response**: 200 OK
```json
[
  {
    "id": 1,
    "provinceCode": "KIG",
    "provinceName": "Kigali City",
    "createdAt": "2025-10-23T10:00:00",
    "updatedAt": "2025-10-23T10:00:00"
  }
]
```

### Get Provinces Sorted
```http
GET /api/provinces/sorted?sortBy=provinceName&direction=ASC
```

### Get Provinces Paginated
```http
GET /api/provinces/paginated?page=0&size=10&sortBy=provinceName
```

### Get Province by ID
```http
GET /api/provinces/{id}
```

### Get Province by Code
```http
GET /api/provinces/code/KIG
```

### Create Province
```http
POST /api/provinces
Content-Type: application/json

{
  "provinceCode": "NEW",
  "provinceName": "New Province"
}
```

### Update Province
```http
PUT /api/provinces/{id}
Content-Type: application/json

{
  "provinceCode": "KIG",
  "provinceName": "Kigali City Updated"
}
```

### Delete Province
```http
DELETE /api/provinces/{id}
```

---

## 👤 User APIs (User-Location Relationship)

### **REQUIREMENT: Get Users by Province Code**
```http
GET /api/users/by-province-code/NOR
```

**Response**: 200 OK
```json
[
  {
    "id": 2,
    "userCode": "USR-FAR-001",
    "firstName": "Jean",
    "lastName": "Uwimana",
    "email": "jean.uwimana@farmer.rw",
    "phoneNumber": "+250788111001",
    "userType": "FARMER",
    "status": "ACTIVE",
    "village": {
      "id": 5,
      "villageCode": "GIT01",
      "villageName": "Gitega 1"
    }
  }
]
```

### **REQUIREMENT: Get Users by Province Name**
```http
GET /api/users/by-province-name/Northern Province
```

### **REQUIREMENT: Get Province from User**
```http
GET /api/users/{id}/province
```

**Response**: 200 OK
```json
{
  "id": 2,
  "provinceCode": "NOR",
  "provinceName": "Northern Province",
  "createdAt": "2025-10-23T10:00:00",
  "updatedAt": "2025-10-23T10:00:00"
}
```

### Get Full Location from User
```http
GET /api/users/{id}/full-location
```

**Response**: 200 OK
```json
{
  "userId": "2",
  "fullLocation": "Gitega 1, Gitega, Muhoza, Musanze, Northern Province"
}
```

### Get Users by Province and Type
```http
GET /api/users/by-province/NOR/type/FARMER
```

### Count Users by Province
```http
GET /api/users/count/by-province/NOR
```

### Create User
```http
POST /api/users
Content-Type: application/json

{
  "userCode": "USR-FAR-999",
  "firstName": "Test",
  "lastName": "Farmer",
  "email": "test@farmer.rw",
  "phoneNumber": "+250788999999",
  "password": "test123",
  "userType": "FARMER",
  "village": {"id": 5}
}
```

### Get Users by Type
```http
GET /api/users/type/FARMER
```

---

## 📦 Inventory APIs

### Get All Inventories
```http
GET /api/inventory
```

### Get Inventories Paginated
```http
GET /api/inventory/paginated?page=0&size=10
```

### Get Inventory by ID
```http
GET /api/inventory/{id}
```

### Get Inventories by Farmer
```http
GET /api/inventory/farmer/{farmerId}
```

### Get Inventories by Warehouse
```http
GET /api/inventory/warehouse/{warehouseId}
```

### Get Inventories by Status
```http
GET /api/inventory/status/STORED
```

### **Get Inventories by Province Code (Deep Relationship)**
```http
GET /api/inventory/by-province/NOR
```

**Response**: 200 OK
```json
[
  {
    "id": 1,
    "inventoryCode": "INV-2024-001",
    "farmer": {
      "id": 2,
      "userCode": "USR-FAR-001",
      "firstName": "Jean"
    },
    "warehouse": {
      "id": 1,
      "warehouseCode": "WH-MUS-001",
      "warehouseName": "Musanze Cooperative Store"
    },
    "cropType": {
      "id": 1,
      "cropCode": "BEAN-001",
      "cropName": "Beans"
    },
    "quantityKg": 500.00,
    "remainingQuantityKg": 500.00,
    "qualityGrade": "A",
    "status": "STORED",
    "storageDate": "2025-10-23"
  }
]
```

### Create Inventory
```http
POST /api/inventory
Content-Type: application/json

{
  "inventoryCode": "INV-2024-999",
  "farmer": {"id": 2},
  "warehouse": {"id": 1},
  "cropType": {"id": 4},
  "storekeeper": {"id": 5},
  "quantityKg": 1000.00,
  "qualityGrade": "A",
  "notes": "High quality beans from Musanze"
}
```

### Reduce Inventory Quantity (Sale)
```http
PATCH /api/inventory/{id}/reduce?quantity=200.50
```

### Withdraw Inventory
```http
PATCH /api/inventory/{id}/withdraw
```

### Get Total Stored Quantity in Warehouse
```http
GET /api/inventory/warehouse/{warehouseId}/total-quantity
```

---

## 📋 Query Parameters Examples

### Sorting
```http
# Sort provinces by name descending
GET /api/provinces/sorted?sortBy=provinceName&direction=DESC

# Sort inventories by date
GET /api/inventory/paginated?page=0&size=10&sortBy=storageDate
```

### Pagination
```http
# Page 0, 10 items per page
GET /api/provinces/paginated?page=0&size=10

# Page 2, 20 items per page
GET /api/users/paginated?page=2&size=20
```

### Combined Filters
```http
# Users in Northern Province who are Farmers
GET /api/users/by-province/NOR/type/FARMER

# Inventories in warehouse with STORED status
GET /api/inventory/warehouse/1?status=STORED
```

---

## 🔍 Query Method Examples Demonstrated

### findBy Methods
```
findByProvinceCode(String code)
findByEmail(String email)
findByUserType(UserType type)
findByWarehouseIdAndStatus(Long id, InventoryStatus status)
findByVillageCellSectorDistrictProvinceProvinceCode(String code)  // NESTED
```

### existsBy Methods
```
existsByEmail(String email)
existsByPhoneNumber(String phone)
existsByProvinceCode(String code)
existsByUserTypeAndStatus(UserType type, UserStatus status)
```

### Sorting
```
Sort.by("provinceName").ascending()
Sort.by("createdAt").descending()
```

### Pagination
```
PageRequest.of(0, 10, Sort.by("name"))
```

### Custom @Query
```sql
@Query("SELECT u FROM User u WHERE u.village.cell.sector.district.province.provinceCode = :provinceCode")
List<User> findUsersByProvinceCode(@Param("provinceCode") String provinceCode);
```

---

## ⚠️ Error Responses

### 404 Not Found
```json
{
  "timestamp": "2025-10-23T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Province not found with code: 'INVALID'",
  "path": "/api/provinces/code/INVALID"
}
```

### 409 Conflict (Duplicate)
```json
{
  "timestamp": "2025-10-23T12:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already registered",
  "path": "/api/users"
}
```

### 400 Bad Request (Validation)
```json
{
  "firstName": "First name is required",
  "email": "Invalid email format"
}
```

---

## 🎯 Key Testing Scenarios

### 1. Test User-Location Relationship (REQUIREMENT)
```bash
# Create a user in Northern Province
POST /api/users
# Body: user with village in Northern Province

# Get all users in Northern Province by code
GET /api/users/by-province-code/NOR

# Get province from specific user
GET /api/users/2/province
# Should return: Northern Province
```

### 2. Test Deep Relationship Queries
```bash
# Get inventories in Northern Province
GET /api/inventory/by-province/NOR
# This demonstrates: Inventory → Warehouse → Village → Cell → Sector → District → Province
```

### 3. Test Pagination and Sorting
```bash
# Get first 5 provinces sorted by name
GET /api/provinces/paginated?page=0&size=5&sortBy=provinceName

# Get second page of users
GET /api/users/paginated?page=1&size=10
```

---

## 🔄 Complete CRUD Examples

### Province CRUD
```bash
# CREATE
POST /api/provinces
Body: {"provinceCode": "TST", "provinceName": "Test Province"}

# READ
GET /api/provinces/code/TST

# UPDATE
PUT /api/provinces/1
Body: {"provinceCode": "TST", "provinceName": "Test Province Updated"}

# DELETE
DELETE /api/provinces/1
```

### User CRUD
```bash
# CREATE
POST /api/users
Body: {full user object}

# READ
GET /api/users/2
GET /api/users/email/jean.uwimana@farmer.rw

# UPDATE
PUT /api/users/2
Body: {updated user object}

# DELETE
DELETE /api/users/2
```

---

## 📊 Sample Data Endpoints

### Check Seeded Data
```bash
# Total provinces (should be 5)
GET /api/provinces/count

# Total users (should be 8)
GET /api/users/count

# Get all farmers
GET /api/users/type/FARMER

# Get all warehouses
GET /api/warehouses
```

---

## 💡 Tips for Testing

1. **Use Swagger UI** for interactive testing: `http://localhost:8080/swagger-ui.html`
2. **Start with GET requests** to see seeded data
3. **Test User-Location endpoints** to verify requirement compliance
4. **Try pagination** with different page sizes
5. **Test sorting** with different fields and directions
6. **Create new resources** and verify relationships

---

## 📞 Support

For questions or issues, check:
- Swagger UI documentation
- Application logs
- README.md file

**Happy API Testing! 🚀**

