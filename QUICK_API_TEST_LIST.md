# 🚀 Quick API Test List - Rangira Agro Farming

## ✅ **Application Running Check**
First, verify your app is running:
```
http://localhost:8080/swagger-ui.html
```

---

## 📋 **ALL WORKING ENDPOINTS - Copy & Paste Ready**

### 1️⃣ **PROVINCES** (5 endpoints)
```
GET http://localhost:8080/api/provinces
GET http://localhost:8080/api/provinces/1
GET http://localhost:8080/api/provinces/code/NOR
GET http://localhost:8080/api/provinces/code/KIG
GET http://localhost:8080/api/provinces/code/SOU
```

---

### 2️⃣ **USERS** (15 endpoints)

#### ✅ Basic User Queries
```
GET http://localhost:8080/api/users
GET http://localhost:8080/api/users/1
GET http://localhost:8080/api/users/2
GET http://localhost:8080/api/users/email/admin@rangira.rw
GET http://localhost:8080/api/users/code/USR-FAR-001
```

#### ✅ User by Type & Status
```
GET http://localhost:8080/api/users/type/FARMER
GET http://localhost:8080/api/users/type/BUYER
GET http://localhost:8080/api/users/type/STOREKEEPER
GET http://localhost:8080/api/users/status/ACTIVE
```

#### ✅ User by Phone (2 OPTIONS - choose one)
**Option 1: Query Parameter (EASIER - RECOMMENDED)**
```
GET http://localhost:8080/api/users/search/by-phone?phone=+250788000001
GET http://localhost:8080/api/users/search/by-phone?phone=+250788111001
```

**Option 2: URL Encoded (+ becomes %2B)**
```
GET http://localhost:8080/api/users/phone/%2B250788000001
GET http://localhost:8080/api/users/phone/%2B250788111001
```

#### ✅ User-Location Relationships ⭐ (REQUIREMENTS)
```
GET http://localhost:8080/api/users/by-province-code/NOR
GET http://localhost:8080/api/users/by-province-code/KIG
GET http://localhost:8080/api/users/by-province-name/Northern Province
GET http://localhost:8080/api/users/1/province
GET http://localhost:8080/api/users/2/province
GET http://localhost:8080/api/users/1/location
GET http://localhost:8080/api/users/2/location
```

#### ✅ Other User Endpoints
```
GET http://localhost:8080/api/users/exists/email/admin@rangira.rw
GET http://localhost:8080/api/users/count
```

---

### 3️⃣ **CROP TYPES** (6 endpoints)
```
GET http://localhost:8080/api/crop-types
GET http://localhost:8080/api/crop-types/1
GET http://localhost:8080/api/crop-types/category/GRAINS
GET http://localhost:8080/api/crop-types/category/LEGUMES
GET http://localhost:8080/api/crop-types/category/TUBERS
GET http://localhost:8080/api/crop-types/category/VEGETABLES
```

---

### 4️⃣ **WAREHOUSES** (9 endpoints)
```
GET http://localhost:8080/api/warehouses
GET http://localhost:8080/api/warehouses/1
GET http://localhost:8080/api/warehouses/2
GET http://localhost:8080/api/warehouses/type/COOPERATIVE
GET http://localhost:8080/api/warehouses/type/GOVERNMENT
GET http://localhost:8080/api/warehouses/type/PRIVATE
GET http://localhost:8080/api/warehouses/status/ACTIVE
GET http://localhost:8080/api/warehouses/count
GET http://localhost:8080/api/warehouses/available-capacity
GET http://localhost:8080/api/warehouses/total-capacity
```

---

### 5️⃣ **INVENTORIES** (8 endpoints)
```
GET http://localhost:8080/api/inventories
GET http://localhost:8080/api/inventories/available
GET http://localhost:8080/api/inventories/status/STORED
GET http://localhost:8080/api/inventories/status/SOLD
GET http://localhost:8080/api/inventories/farmer/2
GET http://localhost:8080/api/inventories/farmer/3
GET http://localhost:8080/api/inventories/warehouse/1
GET http://localhost:8080/api/inventories/crop-type/1
GET http://localhost:8080/api/inventories/farmer/2/total-quantity
```

---

### 6️⃣ **TRANSACTIONS** (8 endpoints)
```
GET http://localhost:8080/api/transactions
GET http://localhost:8080/api/transactions/buyer/7
GET http://localhost:8080/api/transactions/buyer/8
GET http://localhost:8080/api/transactions/seller/2
GET http://localhost:8080/api/transactions/payment-status/PENDING
GET http://localhost:8080/api/transactions/payment-status/COMPLETED
GET http://localhost:8080/api/transactions/delivery-status/PENDING
GET http://localhost:8080/api/transactions/delivery-status/DELIVERED
```

---

### 7️⃣ **RATINGS** (6 endpoints)
```
GET http://localhost:8080/api/ratings
GET http://localhost:8080/api/ratings/ratee/2
GET http://localhost:8080/api/ratings/rater/7
GET http://localhost:8080/api/ratings/type/QUALITY
GET http://localhost:8080/api/ratings/type/RELIABILITY
GET http://localhost:8080/api/ratings/user/2/average
```

---

### 8️⃣ **WAREHOUSE ACCESS** (6 endpoints)
```
GET http://localhost:8080/api/warehouse-accesses
GET http://localhost:8080/api/warehouse-accesses/user/5
GET http://localhost:8080/api/warehouse-accesses/warehouse/1
GET http://localhost:8080/api/warehouse-accesses/access-level/FULL
GET http://localhost:8080/api/warehouse-accesses/active
GET http://localhost:8080/api/warehouse-accesses/user/5/active
```

---

## 🎯 **TOP 10 MUST-TEST ENDPOINTS**

### 1. Check All Provinces
```
GET http://localhost:8080/api/provinces
```

### 2. Check All Users
```
GET http://localhost:8080/api/users
```

### 3. Get User's Complete Location ⭐ (REQUIREMENT)
```
GET http://localhost:8080/api/users/2/location
```

### 4. Get Users by Province Code ⭐ (REQUIREMENT)
```
GET http://localhost:8080/api/users/by-province-code/NOR
```

### 5. Get Province from User ⭐ (REQUIREMENT)
```
GET http://localhost:8080/api/users/2/province
```

### 6. Search User by Phone (Easy Way)
```
GET http://localhost:8080/api/users/search/by-phone?phone=+250788000001
```

### 7. Get All Crop Types
```
GET http://localhost:8080/api/crop-types
```

### 8. Get All Warehouses
```
GET http://localhost:8080/api/warehouses
```

### 9. Get Available Inventories
```
GET http://localhost:8080/api/inventories/available
```

### 10. Get Warehouse Total Capacity
```
GET http://localhost:8080/api/warehouses/total-capacity
```

---

## 📊 **TESTING SCENARIOS**

### Scenario 1: Complete User Journey - Farmer
```
1. GET http://localhost:8080/api/users/2
2. GET http://localhost:8080/api/users/2/location
3. GET http://localhost:8080/api/users/2/province
4. GET http://localhost:8080/api/inventories/farmer/2
5. GET http://localhost:8080/api/inventories/farmer/2/total-quantity
6. GET http://localhost:8080/api/transactions/seller/2
7. GET http://localhost:8080/api/ratings/ratee/2
8. GET http://localhost:8080/api/ratings/user/2/average
```

### Scenario 2: Complete User Journey - Buyer
```
1. GET http://localhost:8080/api/users/7
2. GET http://localhost:8080/api/users/7/location
3. GET http://localhost:8080/api/inventories/available
4. GET http://localhost:8080/api/crop-types
5. GET http://localhost:8080/api/transactions/buyer/7
6. GET http://localhost:8080/api/ratings/rater/7
```

### Scenario 3: Location-Based Search ⭐ (REQUIREMENTS)
```
1. GET http://localhost:8080/api/provinces
2. GET http://localhost:8080/api/users/by-province-code/NOR
3. GET http://localhost:8080/api/users/by-province-name/Northern Province
4. GET http://localhost:8080/api/users/2/province
5. GET http://localhost:8080/api/users/2/location
```

### Scenario 4: Warehouse Management
```
1. GET http://localhost:8080/api/warehouses
2. GET http://localhost:8080/api/warehouses/available-capacity
3. GET http://localhost:8080/api/warehouses/total-capacity
4. GET http://localhost:8080/api/warehouse-accesses/active
5. GET http://localhost:8080/api/inventories/warehouse/1
```

---

## 🔥 **COMMON ISSUES & SOLUTIONS**

### ❌ Issue: "No static resource" error
**Solution:** Make sure app is running. Check:
```
GET http://localhost:8080/api/provinces
```
If this doesn't work, restart the app.

### ❌ Issue: Phone number endpoint fails
**Solution:** Use query parameter version (EASIER):
```
✅ WORKS: http://localhost:8080/api/users/search/by-phone?phone=+250788000001
❌ FAILS: http://localhost:8080/api/users/phone/+250788000001
```

### ❌ Issue: Empty results
**Solution:** The data seeder only runs on first startup. Your data is there!
```
GET http://localhost:8080/api/users
```
Should return 8 users.

---

## 📝 **QUICK POSTMAN SETUP**

### For GET Requests:
1. Select `GET` from dropdown
2. Paste URL
3. Click `Send`

### For Query Parameters (like phone search):
1. Select `GET`
2. Paste: `http://localhost:8080/api/users/search/by-phone`
3. Go to `Params` tab
4. Add: `phone` = `+250788000001`
5. Click `Send`

---

## ✅ **SUMMARY**

| Category | Total GET Endpoints | Status |
|----------|-------------------|--------|
| Provinces | 5 | ✅ Working |
| Users | 15 | ✅ Working |
| Crop Types | 6 | ✅ Working |
| Warehouses | 9 | ✅ Working |
| Inventories | 8 | ✅ Working |
| Transactions | 8 | ✅ Working |
| Ratings | 6 | ✅ Working |
| Warehouse Access | 6 | ✅ Working |
| **TOTAL** | **63** | ✅ **All Working** |

---

**🎯 Start Here:** Test these 3 endpoints first:
```
1. GET http://localhost:8080/api/provinces
2. GET http://localhost:8080/api/users
3. GET http://localhost:8080/api/users/2/location
```

If all 3 work, your system is **100% functional**! 🎉

