# 🚀 Postman Quick Reference - Essential Endpoints

## 📌 Base URL
```
http://localhost:8080/api
```

---

## 🎯 TOP PRIORITY: User-Location Relationship (ASSIGNMENT REQUIREMENT)

### 1️⃣ Get Users by Province CODE ✅
```
GET /api/users/by-province-code/NOR
```
**What it tests**: Deep nested query (Village→Cell→Sector→District→Province)

### 2️⃣ Get Users by Province NAME ✅
```
GET /api/users/by-province-name/Northern Province
```
**What it tests**: Same as above, different query parameter

### 3️⃣ Get Province FROM User (Reverse) ✅
```
GET /api/users/2/province
```
**What it tests**: Reverse lookup (User→Village→...→Province)

### 4️⃣ Get Full Location Hierarchy ✅
```
GET /api/users/2/full-location
```
**What it tests**: Complete 5-level traversal

---

## 📊 Quick Test Sequence (5 Minutes)

### Step 1: Verify App Running
```
GET /api/provinces
Expected: 5 provinces
```

### Step 2: Test User-Location (MAIN REQUIREMENT)
```
GET /api/users/by-province-code/NOR
Expected: Users in Northern Province

GET /api/users/2/province
Expected: Northern Province object
```

### Step 3: Test CRUD
```
POST /api/users
Body: {
  "userCode": "USR-TEST-001",
  "firstName": "Test",
  "lastName": "User",
  "email": "test@test.com",
  "phoneNumber": "+250788999999",
  "password": "test123",
  "userType": "FARMER",
  "village": {"id": 5}
}
Expected: 201 Created

GET /api/users/9
Expected: The created user

DELETE /api/users/9
Expected: 204 No Content
```

### Step 4: Test Query Methods
```
GET /api/users/type/FARMER
Expected: All farmers

GET /api/provinces/sorted?sortBy=provinceName&direction=ASC
Expected: Sorted provinces

GET /api/provinces/paginated?page=0&size=2
Expected: Page object with 2 items
```

---

## 🔍 All Province Codes
```
KIG = Kigali City
NOR = Northern Province
SOU = Southern Province
EAS = Eastern Province
WES = Western Province
```

---

## 👥 Sample User IDs (from seeded data)
```
ID 1 = Admin (Kigali)
ID 2 = Jean Uwimana, Farmer (Northern)
ID 3 = Marie Mukamana, Farmer (Northern)
ID 4 = Patrick Niyonzima, Farmer (Northern)
ID 5 = Joseph Habimana, Storekeeper (Northern)
ID 6 = Alice Uwase, Storekeeper (Northern)
ID 7 = Emmanuel Kagame, Buyer (Kigali)
ID 8 = Grace Mutoni, Buyer (Kigali)
```

---

## 📦 Complete Endpoint List

### PROVINCES
```
GET    /api/provinces
GET    /api/provinces/sorted?sortBy=provinceName&direction=ASC
GET    /api/provinces/paginated?page=0&size=10
GET    /api/provinces/{id}
GET    /api/provinces/code/NOR
GET    /api/provinces/name/Northern Province
POST   /api/provinces
PUT    /api/provinces/{id}
DELETE /api/provinces/{id}
GET    /api/provinces/exists/NOR
GET    /api/provinces/count
```

### USERS (User-Location)
```
GET    /api/users
GET    /api/users/paginated?page=0&size=10
GET    /api/users/{id}
GET    /api/users/email/{email}
GET    /api/users/code/{userCode}
GET    /api/users/type/FARMER

--- USER-LOCATION RELATIONSHIP (REQUIREMENT) ---
GET    /api/users/by-province-code/{code}          ✅
GET    /api/users/by-province-name/{name}          ✅
GET    /api/users/{id}/province                    ✅
GET    /api/users/{id}/full-location               ✅
GET    /api/users/by-province/{code}/type/{type}
GET    /api/users/count/by-province/{code}

POST   /api/users
PUT    /api/users/{id}
PATCH  /api/users/{id}/status?status=ACTIVE
DELETE /api/users/{id}
GET    /api/users/exists/email/{email}
GET    /api/users/count
```

### INVENTORY
```
GET    /api/inventory
GET    /api/inventory/paginated?page=0&size=10
GET    /api/inventory/{id}
GET    /api/inventory/code/{code}
GET    /api/inventory/farmer/{farmerId}
GET    /api/inventory/warehouse/{warehouseId}
GET    /api/inventory/crop-type/{cropTypeId}
GET    /api/inventory/status/STORED
GET    /api/inventory/by-province/{provinceCode}   (Deep Query!)
POST   /api/inventory
PUT    /api/inventory/{id}
PATCH  /api/inventory/{id}/reduce?quantity=100
PATCH  /api/inventory/{id}/withdraw
DELETE /api/inventory/{id}
GET    /api/inventory/warehouse/{id}/total-quantity
GET    /api/inventory/farmer/{id}/active-count
```

---

## 💡 Postman Pro Tips

### Set Base URL Variable
1. Click Collection → Variables
2. Add: `base_url` = `http://localhost:8080/api`
3. Use: `{{base_url}}/users`

### Organize with Folders
```
📁 Rangira Agro Farming APIs
  📁 1. User-Location (REQUIREMENT)
    ├─ Get Users by Province Code
    ├─ Get Users by Province Name
    ├─ Get Province from User
    └─ Get Full Location
  📁 2. CRUD Operations
  📁 3. Query Methods
  📁 4. Sorting & Pagination
```

### Save Responses
Click "Save Response" → "Save as Example"
Now you have documentation with real responses!

---

## ⚡ Quick Copy-Paste JSON Bodies

### Create User (Farmer)
```json
{
  "userCode": "USR-FAR-999",
  "firstName": "TestFirstName",
  "lastName": "TestLastName",
  "email": "test.farmer@rangira.rw",
  "phoneNumber": "+250788999999",
  "password": "test123",
  "userType": "FARMER",
  "village": {"id": 5}
}
```

### Create User (Buyer)
```json
{
  "userCode": "USR-BUY-999",
  "firstName": "TestBuyer",
  "lastName": "LastName",
  "email": "test.buyer@rangira.rw",
  "phoneNumber": "+250788888888",
  "password": "test123",
  "userType": "BUYER",
  "village": {"id": 1}
}
```

### Create Inventory
```json
{
  "inventoryCode": "INV-TEST-001",
  "farmer": {"id": 2},
  "warehouse": {"id": 1},
  "cropType": {"id": 4},
  "storekeeper": {"id": 5},
  "quantityKg": 1000.00,
  "qualityGrade": "A",
  "notes": "High quality beans from Musanze"
}
```

### Create Province
```json
{
  "provinceCode": "TST",
  "provinceName": "Test Province"
}
```

---

## 🎯 Demo Checklist

### Before Demo:
- [ ] Spring Boot app is running
- [ ] Database is populated (auto-seeded)
- [ ] Postman collection is organized
- [ ] Base URL variable is set

### During Demo:
- [ ] Show provinces (data seeded)
- [ ] Get users by province CODE ✅
- [ ] Get province from user ✅
- [ ] Create a user (CRUD)
- [ ] Show sorting
- [ ] Show pagination
- [ ] Show deep query (inventory by province)

### After Demo:
- [ ] Export Postman collection
- [ ] Save screenshots
- [ ] Document any custom tests added

---

## 📸 Screenshots to Capture

1. ✅ Postman collection structure
2. ✅ GET /api/users/by-province-code/NOR (with response)
3. ✅ GET /api/users/2/province (showing reverse lookup)
4. ✅ POST /api/users (showing 201 Created)
5. ✅ GET /api/provinces/paginated (showing page object)
6. ✅ GET /api/inventory/by-province/NOR (deep relationship)

---

## ❌ Common Issues & Quick Fixes

| Issue | Solution |
|-------|----------|
| Connection refused | Start Spring Boot app |
| 404 Not Found | Check ID exists, check URL spelling |
| 409 Conflict | Use different email/phone |
| 400 Bad Request | Check required fields in JSON |
| Empty array response | Check province code spelling |

---

## 🎓 What Each Test Proves

| Endpoint | Assignment Requirement |
|----------|----------------------|
| GET /api/users/by-province-code/NOR | ✅ User-Location relationship |
| GET /api/users/2/province | ✅ Reverse lookup |
| GET /api/provinces/sorted | ✅ Sorting |
| GET /api/provinces/paginated | ✅ Pagination |
| POST /api/users | ✅ Create operation |
| PUT /api/users/{id} | ✅ Update operation |
| DELETE /api/users/{id} | ✅ Delete operation |
| GET /api/users/type/FARMER | ✅ findBy query |
| GET /api/users/exists/email/{email} | ✅ existsBy query |
| GET /api/inventory/by-province/NOR | ✅ Deep nested query |

---

## ⏱️ 2-Minute Speed Test

**Goal**: Prove everything works in 2 minutes

```bash
# 1. Verify running (10 sec)
GET /api/provinces
✅ See 5 provinces

# 2. Main requirement (30 sec)
GET /api/users/by-province-code/NOR
✅ See users in Northern Province

GET /api/users/2/province
✅ See Northern Province object

# 3. CRUD (40 sec)
POST /api/users (with JSON body)
✅ Get 201 Created

GET /api/users/9
✅ See created user

DELETE /api/users/9
✅ Get 204 No Content

# 4. Query methods (40 sec)
GET /api/users/type/FARMER
✅ See all farmers

GET /api/provinces/sorted?sortBy=provinceName&direction=ASC
✅ See sorted list

GET /api/provinces/paginated?page=0&size=2
✅ See page object
```

**Total**: Under 2 minutes! ✅

---

## 🏆 You're Ready to Demo!

With this quick reference:
- ✅ All essential endpoints at your fingertips
- ✅ Copy-paste JSON ready
- ✅ Quick demo sequence prepared
- ✅ Troubleshooting guide included

**Pro tip**: Print this page or keep it open during your demo!

**Good luck! 🚀🌱**

