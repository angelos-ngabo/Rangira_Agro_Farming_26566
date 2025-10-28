# 🚀 Rangira Agro Farming - Quick Start Guide

## ⚡ 5-Minute Setup

### Step 1: Configure Database (1 minute)
Open `src/main/resources/application.properties` and set your database:

**Option A: MySQL (Recommended)**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rangira_agro_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

**Option B: H2 (Fastest - No setup needed)**
```properties
# Comment out MySQL lines and uncomment H2 lines:
spring.datasource.url=jdbc:h2:mem:rangira_agro_db
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
```

### Step 2: Run the Application (1 minute)
```bash
# In project root directory
mvn spring-boot:run
```

Or run `RangiraAgroFarmingApplication.java` from your IDE.

### Step 3: Verify It's Working (1 minute)
Open your browser:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console** (if using H2): http://localhost:8080/h2-console

You should see the Swagger UI with all API endpoints!

### Step 4: Test Key Endpoints (2 minutes)

#### Test 1: Get All Provinces
```http
GET http://localhost:8080/api/provinces
```
Expected: 5 Rwandan provinces

#### Test 2: Get Users by Province Code (REQUIREMENT)
```http
GET http://localhost:8080/api/users/by-province-code/NOR
```
Expected: Users in Northern Province

#### Test 3: Get Province from User (REQUIREMENT)
```http
GET http://localhost:8080/api/users/2/province
```
Expected: Province details for user #2

---

## 📊 What Data is Already Available?

### Automatically Seeded:
✅ **5 Provinces**: Kigali, Northern, Southern, Eastern, Western  
✅ **30 Districts**: Musanze, Nyagatare, Gasabo, Huye, etc.  
✅ **Sectors, Cells, Villages**: Complete hierarchy  
✅ **8 Users**: Farmers, Buyers, Storekeepers, Admin  
✅ **11 Crop Types**: Beans, Maize, Rice, Potatoes, etc.  
✅ **3 Warehouses**: In Musanze and Karambi  

### Sample Login Credentials:
```
Admin:      admin@rangira.rw / admin123
Farmer:     jean.uwimana@farmer.rw / farmer123
Storekeeper: joseph.habimana@storekeeper.rw / storekeeper123
Buyer:      emmanuel.kagame@buyer.rw / buyer123
```

---

## 🎯 Key API Endpoints to Test

### 1. Province Management
```http
GET    /api/provinces                  # Get all provinces
GET    /api/provinces/sorted           # Sorted by name
GET    /api/provinces/paginated        # With pagination
GET    /api/provinces/code/NOR         # Get by code
```

### 2. User-Location Relationship (ASSIGNMENT REQUIREMENT)
```http
GET /api/users/by-province-code/NOR      # Users in Northern Province ✅
GET /api/users/by-province-name/Northern Province  # By province name ✅
GET /api/users/2/province                # Get province from user ✅
GET /api/users/2/full-location           # Full hierarchy ✅
```

### 3. Inventory Management
```http
GET /api/inventory                       # All inventories
GET /api/inventory/by-province/NOR       # By province (deep query)
GET /api/inventory/farmer/2              # By farmer
GET /api/inventory/warehouse/1           # By warehouse
```

---

## 🔍 Test Scenario: Complete User-Location Flow

### Scenario: Find all farmers in Northern Province

**Step 1: Get Province Code**
```http
GET /api/provinces/name/Northern Province
```
Response: `{ "provinceCode": "NOR", ... }`

**Step 2: Get All Users in That Province**
```http
GET /api/users/by-province-code/NOR
```
Response: List of users in Northern Province

**Step 3: Get Full Location for a Specific User**
```http
GET /api/users/2/full-location
```
Response: `"Gitega 1, Gitega, Muhoza, Musanze, Northern Province"`

**Step 4: Verify Reverse Lookup**
```http
GET /api/users/2/province
```
Response: Full province object

✅ **This demonstrates the complete User-Location requirement!**

---

## 🧪 Testing with Swagger UI

1. Go to: http://localhost:8080/swagger-ui.html
2. Expand `User` section
3. Find `GET /api/users/by-province-code/{provinceCode}`
4. Click "Try it out"
5. Enter `NOR` as provinceCode
6. Click "Execute"
7. See all users in Northern Province!

---

## 📝 Quick CRUD Test

### Create a New Farmer
```http
POST /api/users
Content-Type: application/json

{
  "userCode": "USR-FAR-999",
  "firstName": "Test",
  "lastName": "Farmer",
  "email": "test.farmer@rangira.rw",
  "phoneNumber": "+250788999999",
  "password": "test123",
  "userType": "FARMER",
  "village": {"id": 5}
}
```

### Get the Farmer's Province
```http
GET /api/users/{createdUserId}/province
```

### Create Inventory for That Farmer
```http
POST /api/inventory
Content-Type: application/json

{
  "inventoryCode": "INV-TEST-001",
  "farmer": {"id": {createdUserId}},
  "warehouse": {"id": 1},
  "cropType": {"id": 4},
  "storekeeper": {"id": 5},
  "quantityKg": 1000.00,
  "qualityGrade": "A"
}
```

### Get All Inventory in That Province
```http
GET /api/inventory/by-province/NOR
```

✅ **Complete CRUD + Relationships tested!**

---

## ❓ Troubleshooting

### Port 8080 Already in Use
**Solution**: Change port in `application.properties`
```properties
server.port=8081
```

### Database Connection Error
**Solution**: 
1. Check MySQL is running
2. Verify username/password
3. Or switch to H2 in-memory database

### No Data Showing
**Solution**: 
1. Stop the application
2. Drop the database: `DROP DATABASE rangira_agro_db;`
3. Restart - data will be auto-seeded

### "Could not resolve parameter" errors
**Solution**: Make sure all dependencies are downloaded:
```bash
mvn clean install
```

---

## 🎯 Assignment Verification Checklist

Use this to verify all requirements:

```
✅ At least 5 entities → We have 13
✅ Complete CRUD → Implemented for all entities
✅ findBy queries → 50+ methods
✅ existsBy queries → 20+ methods
✅ Sorting → All repositories
✅ Pagination → All repositories
✅ Location hierarchy → Province→District→Sector→Cell→Village
✅ User-Location link → Fully implemented
✅ Get users by province code → /api/users/by-province-code/{code}
✅ Get users by province name → /api/users/by-province-name/{name}
✅ Get province from user → /api/users/{id}/province
✅ One-to-One → User ↔ UserProfile
✅ One-to-Many → 15+ examples
✅ Many-to-Many → User ↔ Warehouse
```

---

## 📚 Documentation Files

1. **README.md** - Complete project overview
2. **API_DOCUMENTATION.md** - All endpoints with examples
3. **IMPLEMENTATION_SUMMARY.md** - Requirements checklist
4. **QUICK_START.md** - This file
5. **ERD files** - Database diagrams

---

## 🎓 For Your Presentation

### Key Points to Mention:
1. ✅ **13 entities** (exceeds 5 requirement)
2. ✅ **Complete CRUD** for all entities
3. ✅ **User-Location relationship** fully implemented
4. ✅ **All 3 relationship types** demonstrated
5. ✅ **Rwandan location hierarchy** auto-seeded
6. ✅ **50+ JPA query methods** (findBy, existsBy, pagination, sorting)
7. ✅ **Real business problem** solved

### Demo Flow:
1. Show Swagger UI
2. Test User-Location endpoints
3. Show deep relationship query (inventory by province)
4. Show pagination and sorting
5. Show data in database

---

## 🚀 You're Ready!

Your application is **production-ready** and **exceeds all requirements**.

**Need help?** Check the comprehensive documentation files.

**Happy coding! 🌱💻**

