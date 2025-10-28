# 🧪 Rangira Agro Farming - Complete Postman Testing Guide

## 📋 Table of Contents
1. [Setup Postman](#setup-postman)
2. [Test Basic Endpoints](#test-basic-endpoints)
3. [Test User-Location Relationship (MAIN REQUIREMENT)](#test-user-location-requirement)
4. [Test CRUD Operations](#test-crud-operations)
5. [Test JPA Query Methods](#test-jpa-query-methods)
6. [Test Sorting & Pagination](#test-sorting-pagination)
7. [Complete Business Workflow](#complete-workflow)
8. [Postman Collection Export](#postman-collection)

---

## 🚀 Step 1: Setup Postman

### 1.1 Download & Install Postman
1. Go to https://www.postman.com/downloads/
2. Download for Windows
3. Install and create a free account (optional but recommended)

### 1.2 Create a New Collection
1. Open Postman
2. Click "Collections" in left sidebar
3. Click "+" or "Create Collection"
4. Name it: **"Rangira Agro Farming APIs"**
5. Click "Create"

### 1.3 Set Base URL Variable (Optional but Recommended)
1. Click on your collection
2. Go to "Variables" tab
3. Add variable:
   - **Variable**: `base_url`
   - **Initial Value**: `http://localhost:8080/api`
   - **Current Value**: `http://localhost:8080/api`
4. Click "Save"

Now you can use `{{base_url}}` in all requests!

---

## ✅ Step 2: Test Basic Endpoints (Verify App is Running)

### Test 1: Get All Provinces

**Purpose**: Verify application is running and data is seeded

1. Click "Add Request" in your collection
2. Name: `Get All Provinces`
3. Method: **GET**
4. URL: `http://localhost:8080/api/provinces`
   - Or with variable: `{{base_url}}/provinces`
5. Click **Send**

**Expected Response** (200 OK):
```json
[
  {
    "id": 1,
    "provinceCode": "KIG",
    "provinceName": "Kigali City",
    "createdAt": "2025-10-23T10:30:00",
    "updatedAt": "2025-10-23T10:30:00"
  },
  {
    "id": 2,
    "provinceCode": "NOR",
    "provinceName": "Northern Province",
    "createdAt": "2025-10-23T10:30:00",
    "updatedAt": "2025-10-23T10:30:00"
  },
  {
    "id": 3,
    "provinceCode": "SOU",
    "provinceName": "Southern Province",
    "createdAt": "2025-10-23T10:30:00",
    "updatedAt": "2025-10-23T10:30:00"
  },
  {
    "id": 4,
    "provinceCode": "EAS",
    "provinceName": "Eastern Province",
    "createdAt": "2025-10-23T10:30:00",
    "updatedAt": "2025-10-23T10:30:00"
  },
  {
    "id": 5,
    "provinceCode": "WES",
    "provinceName": "Western Province",
    "createdAt": "2025-10-23T10:30:00",
    "updatedAt": "2025-10-23T10:30:00"
  }
]
```

✅ **Success**: You should see 5 provinces!

---

### Test 2: Get All Users

1. Add new request: `Get All Users`
2. Method: **GET**
3. URL: `{{base_url}}/users`
4. Click **Send**

**Expected Response**: Array of 8 users (1 Admin, 3 Farmers, 2 Storekeepers, 2 Buyers)

✅ **Success**: You should see users with different userTypes!

---

## 🎯 Step 3: Test User-Location Relationship (MAIN ASSIGNMENT REQUIREMENT)

This is the **MOST IMPORTANT** section for your assignment!

### Test 3.1: Get Users by Province Code ✅ REQUIREMENT

**Purpose**: Demonstrate the key requirement - get users by province code

1. Add new request: `Get Users by Province Code (NOR)`
2. Method: **GET**
3. URL: `{{base_url}}/users/by-province-code/NOR`
4. Click **Send**

**Expected Response** (200 OK):
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
    },
    "createdAt": "2025-10-23T10:30:00",
    "updatedAt": "2025-10-23T10:30:00"
  },
  {
    "id": 3,
    "userCode": "USR-FAR-002",
    "firstName": "Marie",
    "lastName": "Mukamana",
    "email": "marie.mukamana@farmer.rw",
    "phoneNumber": "+250788111002",
    "userType": "FARMER",
    "status": "ACTIVE",
    "village": {
      "id": 5,
      "villageCode": "GIT01",
      "villageName": "Gitega 1"
    }
  }
  // ... more users
]
```

✅ **This demonstrates**: Deep nested query through Village → Cell → Sector → District → Province!

**Try other province codes**:
- `{{base_url}}/users/by-province-code/KIG` (Kigali)
- `{{base_url}}/users/by-province-code/EAS` (Eastern)
- `{{base_url}}/users/by-province-code/SOU` (Southern)

---

### Test 3.2: Get Users by Province Name ✅ REQUIREMENT

1. Add new request: `Get Users by Province Name`
2. Method: **GET**
3. URL: `{{base_url}}/users/by-province-name/Northern Province`
4. Click **Send**

**Expected Response**: Same users as Test 3.1 (users in Northern Province)

📝 **Note**: For spaces in URL, Postman automatically encodes them. You can also use: `/Northern%20Province`

---

### Test 3.3: Get Province from User (Reverse Lookup) ✅ REQUIREMENT

**Purpose**: Get province information from a user ID (reverse relationship)

1. Add new request: `Get Province from User`
2. Method: **GET**
3. URL: `{{base_url}}/users/2/province`
   - User ID 2 is Jean Uwimana (from Northern Province)
4. Click **Send**

**Expected Response** (200 OK):
```json
{
  "id": 2,
  "provinceCode": "NOR",
  "provinceName": "Northern Province",
  "createdAt": "2025-10-23T10:30:00",
  "updatedAt": "2025-10-23T10:30:00"
}
```

✅ **This demonstrates**: Reverse lookup - from User → Village → Cell → Sector → District → Province!

**Try other user IDs**:
- `{{base_url}}/users/1/province` (Admin in Kigali)
- `{{base_url}}/users/7/province` (Buyer in Kigali)

---

### Test 3.4: Get Full Location Hierarchy from User

**Purpose**: Get complete location details (Village, Cell, Sector, District, Province)

1. Add new request: `Get Full Location from User`
2. Method: **GET**
3. URL: `{{base_url}}/users/2/full-location`
4. Click **Send**

**Expected Response** (200 OK):
```json
{
  "userId": "2",
  "fullLocation": "Gitega 1, Gitega, Muhoza, Musanze, Northern Province"
}
```

✅ **This demonstrates**: Complete 5-level hierarchy traversal!

---

### Test 3.5: Get Users by Province and User Type

**Purpose**: Combined filter - province code + user type

1. Add new request: `Get Farmers in Northern Province`
2. Method: **GET**
3. URL: `{{base_url}}/users/by-province/NOR/type/FARMER`
4. Click **Send**

**Expected Response**: Only FARMER users in Northern Province

**Try other combinations**:
- `{{base_url}}/users/by-province/KIG/type/ADMIN`
- `{{base_url}}/users/by-province/KIG/type/BUYER`

---

### Test 3.6: Count Users by Province

1. Add new request: `Count Users in Northern Province`
2. Method: **GET**
3. URL: `{{base_url}}/users/count/by-province/NOR`
4. Click **Send**

**Expected Response** (200 OK):
```json
5
```

---

## 📝 Step 4: Test CRUD Operations

### Test 4.1: CREATE - Add New Farmer

1. Add new request: `Create New Farmer`
2. Method: **POST**
3. URL: `{{base_url}}/users`
4. Go to **Body** tab
5. Select **raw** and **JSON** from dropdown
6. Enter this JSON:

```json
{
  "userCode": "USR-FAR-999",
  "firstName": "TestFirstName",
  "lastName": "TestLastName",
  "email": "test.farmer@rangira.rw",
  "phoneNumber": "+250788999999",
  "password": "test123",
  "userType": "FARMER",
  "village": {
    "id": 5
  }
}
```

7. Click **Send**

**Expected Response** (201 Created):
```json
{
  "id": 9,
  "userCode": "USR-FAR-999",
  "firstName": "TestFirstName",
  "lastName": "TestLastName",
  "email": "test.farmer@rangira.rw",
  "phoneNumber": "+250788999999",
  "userType": "FARMER",
  "status": "ACTIVE",
  "village": {
    "id": 5,
    "villageCode": "GIT01",
    "villageName": "Gitega 1"
  },
  "createdAt": "2025-10-23T12:00:00",
  "updatedAt": "2025-10-23T12:00:00"
}
```

✅ **Copy the returned ID** - you'll need it for update/delete tests!

---

### Test 4.2: READ - Get User by ID

1. Add new request: `Get User by ID`
2. Method: **GET**
3. URL: `{{base_url}}/users/9` (use the ID from Test 4.1)
4. Click **Send**

**Expected Response**: The user you just created

---

### Test 4.3: READ - Get User by Email

1. Add new request: `Get User by Email`
2. Method: **GET**
3. URL: `{{base_url}}/users/email/test.farmer@rangira.rw`
4. Click **Send**

**Expected Response**: Same user

---

### Test 4.4: UPDATE - Update User

1. Add new request: `Update User`
2. Method: **PUT**
3. URL: `{{base_url}}/users/9`
4. Body (raw JSON):

```json
{
  "userCode": "USR-FAR-999",
  "firstName": "UpdatedFirstName",
  "lastName": "UpdatedLastName",
  "email": "test.farmer@rangira.rw",
  "phoneNumber": "+250788999999",
  "userType": "FARMER",
  "village": {
    "id": 5
  }
}
```

5. Click **Send**

**Expected Response** (200 OK): User with updated names

---

### Test 4.5: DELETE - Delete User

1. Add new request: `Delete User`
2. Method: **DELETE**
3. URL: `{{base_url}}/users/9`
4. Click **Send**

**Expected Response** (204 No Content): Empty response

**Verify deletion**:
- Try `GET {{base_url}}/users/9`
- Should get 404 error

---

## 🔍 Step 5: Test JPA Query Methods

### Test 5.1: findBy - Get Users by Type

1. Add new request: `Get All Farmers`
2. Method: **GET**
3. URL: `{{base_url}}/users/type/FARMER`
4. Click **Send**

**Expected Response**: All users with userType = FARMER

**Try other types**:
- `{{base_url}}/users/type/BUYER`
- `{{base_url}}/users/type/STOREKEEPER`
- `{{base_url}}/users/type/ADMIN`

---

### Test 5.2: findBy - Get Province by Code

1. Add new request: `Get Province by Code`
2. Method: **GET**
3. URL: `{{base_url}}/provinces/code/NOR`
4. Click **Send**

**Expected Response**: Northern Province object

---

### Test 5.3: existsBy - Check if Email Exists

1. Add new request: `Check Email Exists`
2. Method: **GET**
3. URL: `{{base_url}}/users/exists/email/jean.uwimana@farmer.rw`
4. Click **Send**

**Expected Response** (200 OK):
```json
true
```

**Try non-existent email**:
- `{{base_url}}/users/exists/email/notexist@test.com`
- Should return: `false`

---

### Test 5.4: Custom Query - Get Inventories by Province

**Purpose**: Demonstrate deep relationship query

1. Add new request: `Get Inventories in Northern Province`
2. Method: **GET**
3. URL: `{{base_url}}/inventory/by-province/NOR`
4. Click **Send**

**Expected Response**: Inventories where Warehouse is in Northern Province

✅ **This demonstrates**: Custom @Query with deep nesting:  
Inventory → Warehouse → Village → Cell → Sector → District → Province

---

## 📊 Step 6: Test Sorting & Pagination

### Test 6.1: Sorting - Get Provinces Sorted

1. Add new request: `Get Provinces Sorted Ascending`
2. Method: **GET**
3. URL: `{{base_url}}/provinces/sorted?sortBy=provinceName&direction=ASC`
4. Click **Send**

**Expected Response**: Provinces sorted alphabetically A-Z

**Try descending**:
- `{{base_url}}/provinces/sorted?sortBy=provinceName&direction=DESC`
- Should be Z-A order

---

### Test 6.2: Pagination - Get Provinces with Pagination

1. Add new request: `Get Provinces Page 1`
2. Method: **GET**
3. URL: `{{base_url}}/provinces/paginated?page=0&size=2&sortBy=provinceName`
4. Click **Send**

**Expected Response** (200 OK):
```json
{
  "content": [
    // First 2 provinces
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2
  },
  "totalPages": 3,
  "totalElements": 5,
  "size": 2,
  "number": 0,
  "first": true,
  "last": false
}
```

**Try page 2**:
- `{{base_url}}/provinces/paginated?page=1&size=2&sortBy=provinceName`

---

### Test 6.3: Pagination - Get Users Paginated

1. Add new request: `Get Users Paginated`
2. Method: **GET**
3. URL: `{{base_url}}/users/paginated?page=0&size=5`
4. Click **Send**

**Expected Response**: Page object with 5 users per page

---

## 🎯 Step 7: Complete Business Workflow Test

### Scenario: Farmer stores crops, Buyer purchases

#### Step 7.1: Create Inventory (Farmer stores beans)

1. Add new request: `Create Inventory`
2. Method: **POST**
3. URL: `{{base_url}}/inventory`
4. Body (raw JSON):

```json
{
  "inventoryCode": "INV-TEST-001",
  "farmer": {
    "id": 2
  },
  "warehouse": {
    "id": 1
  },
  "cropType": {
    "id": 4
  },
  "storekeeper": {
    "id": 5
  },
  "quantityKg": 1000.00,
  "qualityGrade": "A",
  "notes": "High quality beans from Musanze"
}
```

5. Click **Send**

**Expected Response** (201 Created):
```json
{
  "id": 1,
  "inventoryCode": "INV-TEST-001",
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
    "id": 4,
    "cropCode": "BEAN-001",
    "cropName": "Beans"
  },
  "storekeeper": {
    "id": 5,
    "userCode": "USR-STK-001"
  },
  "quantityKg": 1000.00,
  "remainingQuantityKg": 1000.00,
  "qualityGrade": "A",
  "status": "STORED",
  "storageDate": "2025-10-23",
  "createdAt": "2025-10-23T12:00:00"
}
```

---

#### Step 7.2: Get Inventory by Farmer

1. Add new request: `Get Farmer's Inventories`
2. Method: **GET**
3. URL: `{{base_url}}/inventory/farmer/2`
4. Click **Send**

**Expected Response**: All inventories for farmer ID 2

---

#### Step 7.3: Get Inventory by Warehouse

1. Add new request: `Get Warehouse Inventories`
2. Method: **GET**
3. URL: `{{base_url}}/inventory/warehouse/1`
4. Click **Send**

**Expected Response**: All inventories in warehouse ID 1

---

#### Step 7.4: Reduce Inventory (Simulate Sale)

1. Add new request: `Reduce Inventory Quantity`
2. Method: **PATCH**
3. URL: `{{base_url}}/inventory/1/reduce?quantity=300.50`
4. Click **Send**

**Expected Response** (200 OK):
```json
{
  "id": 1,
  "inventoryCode": "INV-TEST-001",
  "quantityKg": 1000.00,
  "remainingQuantityKg": 699.50,
  "status": "PARTIALLY_SOLD",
  // ... rest of data
}
```

✅ **Notice**: 
- `remainingQuantityKg` decreased from 1000 to 699.50
- `status` changed to "PARTIALLY_SOLD"

---

#### Step 7.5: Get Total Stored Quantity in Warehouse

1. Add new request: `Get Warehouse Total Quantity`
2. Method: **GET**
3. URL: `{{base_url}}/inventory/warehouse/1/total-quantity`
4. Click **Send**

**Expected Response** (200 OK):
```json
699.50
```

---

## 📦 Step 8: Export Postman Collection

### Save Your Tests for Reuse

1. Click on your collection "Rangira Agro Farming APIs"
2. Click the three dots (•••) next to collection name
3. Click "Export"
4. Choose "Collection v2.1 (recommended)"
5. Click "Export"
6. Save as `Rangira_Agro_Farming_APIs.postman_collection.json`

**To import later**:
- Click "Import" in Postman
- Select your JSON file
- All requests will be restored!

---

## 🎯 Testing Checklist for Your Presentation

Use this checklist during your demo:

### ✅ Assignment Requirements
```
□ Get all provinces (verify data seeded)
□ Get users by province CODE (NOR) ✅ REQUIREMENT
□ Get users by province NAME (Northern Province) ✅ REQUIREMENT
□ Get province from user ✅ REQUIREMENT
□ Get full location hierarchy from user
□ Get users by province and type (combined filter)
□ Count users by province
```

### ✅ CRUD Operations
```
□ CREATE: Add new farmer
□ READ: Get farmer by ID
□ READ: Get farmer by email
□ UPDATE: Update farmer details
□ DELETE: Delete farmer
```

### ✅ JPA Query Methods
```
□ findBy: Get users by type (FARMER, BUYER, etc.)
□ findBy: Get province by code
□ existsBy: Check email exists
□ Custom @Query: Get inventories by province
```

### ✅ Sorting & Pagination
```
□ Sorting: Get provinces sorted ASC/DESC
□ Pagination: Get provinces page 1, page 2
□ Pagination: Get users with page size 5
```

### ✅ Relationship Types
```
□ One-to-One: User has UserProfile
□ One-to-Many: Province has many Districts
□ One-to-Many: Village has many Users
□ Many-to-Many: User-Warehouse access
□ Deep Nesting: Inventory by province (5-level query)
```

---

## 🔥 Pro Tips for Postman

### Tip 1: Use Environment Variables
Instead of hardcoding URLs, use `{{base_url}}` variable

### Tip 2: Organize with Folders
Create folders in your collection:
- 📁 1. User-Location Relationship (REQUIREMENT)
- 📁 2. CRUD Operations
- 📁 3. JPA Query Methods
- 📁 4. Sorting & Pagination
- 📁 5. Business Workflows

### Tip 3: Add Descriptions
Click on each request → "Documentation" tab → Add description explaining what it tests

### Tip 4: Use Tests Tab (Advanced)
Add automatic validation:
```javascript
// In "Tests" tab of request
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has provinces", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
    pm.expect(jsonData.length).to.be.above(0);
});
```

### Tip 5: Save Responses
Click "Save Response" to keep examples for documentation

---

## ❌ Common Errors & Solutions

### Error 1: Connection Refused
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```
**Solution**: Make sure Spring Boot app is running (`mvn spring-boot:run`)

---

### Error 2: 404 Not Found
```json
{
  "timestamp": "2025-10-23T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 99"
}
```
**Solution**: Use valid IDs (check `GET /api/users` for valid IDs)

---

### Error 3: 409 Conflict (Duplicate)
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already registered"
}
```
**Solution**: Use different email/phone number in POST requests

---

### Error 4: 400 Bad Request (Validation)
```json
{
  "firstName": "First name is required",
  "email": "Invalid email format"
}
```
**Solution**: Check required fields and format in request body

---

## 🎓 Testing Flow for Presentation

### Recommended Demo Order:

1. **Start Simple** (30 seconds)
   - GET `/api/provinces` - Show data is seeded

2. **Main Requirement** (2 minutes)
   - GET `/api/users/by-province-code/NOR` ✅
   - GET `/api/users/2/province` ✅
   - GET `/api/users/2/full-location` ✅
   - Explain the deep nesting

3. **CRUD** (1 minute)
   - POST `/api/users` - Create farmer
   - GET `/api/users/{id}` - Read it back
   - DELETE `/api/users/{id}` - Delete it

4. **Query Methods** (1 minute)
   - GET `/api/users/type/FARMER` - findBy
   - GET `/api/provinces/sorted` - Sorting
   - GET `/api/provinces/paginated` - Pagination

5. **Business Logic** (1 minute)
   - POST `/api/inventory` - Store crops
   - PATCH `/api/inventory/{id}/reduce` - Reduce quantity

**Total time**: 5-6 minutes (perfect for demo!)

---

## 📸 Screenshots to Take

For your documentation/presentation:
1. ✅ Postman collection with all requests
2. ✅ GET users by province code - showing multiple results
3. ✅ GET province from user - showing reverse lookup
4. ✅ POST create user - showing 201 Created
5. ✅ GET paginated - showing page object
6. ✅ Custom query - inventories by province

---

## 🎉 You're Ready!

With this guide, you can:
- ✅ Test all assignment requirements
- ✅ Demonstrate all relationship types
- ✅ Show all JPA query methods
- ✅ Prove CRUD operations work
- ✅ Present with confidence!

**Good luck with your demo! 🚀**

