# 🚀 Complete API Testing Guide - Rangira Agro Farming

## 📋 Table of Contents
1. [Location APIs](#1-location-apis) - Provinces, Districts, Sectors, Cells, Villages
2. [User APIs](#2-user-apis) - User management and location relationships
3. [Crop Type APIs](#3-crop-type-apis) - Crop type management
4. [Warehouse APIs](#4-warehouse-apis) - Storage warehouse management
5. [Inventory APIs](#5-inventory-apis) - Crop inventory management
6. [Transaction APIs](#6-transaction-apis) - Buying/selling transactions
7. [Rating APIs](#7-rating-apis) - User rating and trust system
8. [Warehouse Access APIs](#8-warehouse-access-apis) - Access control management

---

## 🌐 Base URL
```
http://localhost:8080
```

## 🎯 Swagger UI (Interactive Testing)
```
http://localhost:8080/swagger-ui.html
```

---

# 1️⃣ LOCATION APIs

## 1.1 Province APIs

### ✅ Get All Provinces
```
GET http://localhost:8080/api/provinces
```

### ✅ Get Province by ID
```
GET http://localhost:8080/api/provinces/1
```

### ✅ Get Province by Code
```
GET http://localhost:8080/api/provinces/code/KIG
```
**Other codes:** NOR, SOU, EAS, WES

### ✅ Create New Province
```
POST http://localhost:8080/api/provinces
Content-Type: application/json

{
  "provinceCode": "TEST",
  "provinceName": "Test Province"
}
```

### ✅ Update Province
```
PUT http://localhost:8080/api/provinces/1
Content-Type: application/json

{
  "provinceCode": "KIG",
  "provinceName": "Kigali City - Updated"
}
```

### ✅ Delete Province
```
DELETE http://localhost:8080/api/provinces/1
```

---

# 2️⃣ USER APIs

## 2.1 Basic User CRUD

### ✅ Get All Users
```
GET http://localhost:8080/api/users
```

### ✅ Get User by ID
```
GET http://localhost:8080/api/users/1
```

### ✅ Create New User
```
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "userCode": "USR-TEST-001",
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.rw",
  "phoneNumber": "+250788999999",
  "password": "password123",
  "userType": "FARMER",
  "status": "ACTIVE",
  "village": {
    "id": 1
  }
}
```

**User Types:** FARMER, BUYER, STOREKEEPER, ADMIN

### ✅ Update User
```
PUT http://localhost:8080/api/users/1
Content-Type: application/json

{
  "userCode": "USR-ADM-001",
  "firstName": "Admin",
  "lastName": "User Updated",
  "email": "admin@rangira.rw",
  "phoneNumber": "+250788000001",
  "password": "newpassword123",
  "userType": "ADMIN",
  "status": "ACTIVE",
  "village": {
    "id": 1
  }
}
```

### ✅ Delete User
```
DELETE http://localhost:8080/api/users/1
```

## 2.2 User-Location Relationship APIs

### ✅ Get Users by Province Code
```
GET http://localhost:8080/api/users/by-province-code/NOR
```

### ✅ Get Users by Province Name
```
GET http://localhost:8080/api/users/by-province-name/Northern Province
```

### ✅ Get Province by User ID
```
GET http://localhost:8080/api/users/1/province
```

### ✅ Get Complete Location Hierarchy for User
```
GET http://localhost:8080/api/users/1/location
```
**Returns:** Village → Cell → Sector → District → Province

## 2.3 User Search & Filter APIs

### ✅ Get Users by Type
```
GET http://localhost:8080/api/users/type/FARMER
```
**Types:** FARMER, BUYER, STOREKEEPER, ADMIN

### ✅ Get Users by Status
```
GET http://localhost:8080/api/users/status/ACTIVE
```
**Statuses:** ACTIVE, INACTIVE, SUSPENDED

### ✅ Search Users by Email
```
GET http://localhost:8080/api/users/email/admin@rangira.rw
```

### ✅ Search Users by Phone

**Option 1: Using Query Parameter (RECOMMENDED - Easier)**
```
GET http://localhost:8080/api/users/search/by-phone?phone=+250788000001
```

**Option 2: Using Path Variable (URL-encoded, + becomes %2B)**
```
GET http://localhost:8080/api/users/phone/%2B250788000001
```

### ✅ Check if Email Exists
```
GET http://localhost:8080/api/users/exists/email/test@example.rw
```

---

# 3️⃣ CROP TYPE APIs

### ✅ Get All Crop Types
```
GET http://localhost:8080/api/crop-types
```

### ✅ Get Crop Type by ID
```
GET http://localhost:8080/api/crop-types/1
```

### ✅ Get Crop Types by Category
```
GET http://localhost:8080/api/crop-types/category/GRAINS
```
**Categories:** GRAINS, LEGUMES, TUBERS, VEGETABLES, FRUITS

### ✅ Create New Crop Type
```
POST http://localhost:8080/api/crop-types
Content-Type: application/json

{
  "cropCode": "CROP-TEST-001",
  "cropName": "Test Crop",
  "category": "VEGETABLES",
  "measurementUnit": "KG",
  "description": "This is a test crop type"
}
```

**Measurement Units:** KG, TONS, BAGS, CRATES, BUNCHES

### ✅ Update Crop Type
```
PUT http://localhost:8080/api/crop-types/1
Content-Type: application/json

{
  "cropCode": "CROP-MAI-001",
  "cropName": "Maize - Updated",
  "category": "GRAINS",
  "measurementUnit": "BAGS",
  "description": "Updated description"
}
```

### ✅ Delete Crop Type
```
DELETE http://localhost:8080/api/crop-types/1
```

---

# 4️⃣ WAREHOUSE APIs

### ✅ Get All Warehouses
```
GET http://localhost:8080/api/warehouses
```

### ✅ Get Warehouse by ID
```
GET http://localhost:8080/api/warehouses/1
```

### ✅ Get Warehouses by Type
```
GET http://localhost:8080/api/warehouses/type/COOPERATIVE
```
**Types:** COOPERATIVE, PRIVATE, GOVERNMENT

### ✅ Get Warehouses by Status
```
GET http://localhost:8080/api/warehouses/status/ACTIVE
```
**Statuses:** ACTIVE, INACTIVE, FULL, MAINTENANCE

### ✅ Get Warehouses with Available Capacity
```
GET http://localhost:8080/api/warehouses/available-capacity
```

### ✅ Get Total Storage Capacity (All Warehouses)
```
GET http://localhost:8080/api/warehouses/total-capacity
```

### ✅ Create New Warehouse
```
POST http://localhost:8080/api/warehouses
Content-Type: application/json

{
  "warehouseCode": "WH-TEST-001",
  "warehouseName": "Test Warehouse",
  "warehouseType": "PRIVATE",
  "totalCapacityKg": 25000.00,
  "availableCapacityKg": 25000.00,
  "status": "ACTIVE",
  "village": {
    "id": 1
  }
}
```

### ✅ Update Warehouse
```
PUT http://localhost:8080/api/warehouses/1
Content-Type: application/json

{
  "warehouseCode": "WH-MUS-001",
  "warehouseName": "Musanze Cooperative Store - Updated",
  "warehouseType": "COOPERATIVE",
  "totalCapacityKg": 60000.00,
  "availableCapacityKg": 50000.00,
  "status": "ACTIVE",
  "village": {
    "id": 3
  }
}
```

### ✅ Delete Warehouse
```
DELETE http://localhost:8080/api/warehouses/1
```

---

# 5️⃣ INVENTORY APIs

### ✅ Get All Inventories
```
GET http://localhost:8080/api/inventories
```

### ✅ Get Inventory by ID
```
GET http://localhost:8080/api/inventories/1
```

### ✅ Get Inventories by Farmer
```
GET http://localhost:8080/api/inventories/farmer/2
```

### ✅ Get Inventories by Warehouse
```
GET http://localhost:8080/api/inventories/warehouse/1
```

### ✅ Get Inventories by Crop Type
```
GET http://localhost:8080/api/inventories/crop-type/1
```

### ✅ Get Inventories by Status
```
GET http://localhost:8080/api/inventories/status/AVAILABLE
```
**Statuses:** AVAILABLE, RESERVED, SOLD, EXPIRED, DAMAGED

### ✅ Get Available Inventories
```
GET http://localhost:8080/api/inventories/available
```

### ✅ Get Total Quantity by Farmer
```
GET http://localhost:8080/api/inventories/farmer/2/total-quantity
```

### ✅ Create New Inventory
```
POST http://localhost:8080/api/inventories
Content-Type: application/json

{
  "inventoryCode": "INV-TEST-001",
  "farmer": {
    "id": 2
  },
  "cropType": {
    "id": 1
  },
  "warehouse": {
    "id": 1
  },
  "storekeeper": {
    "id": 5
  },
  "quantityKg": 500.00,
  "pricePerKg": 850.00,
  "storageDate": "2025-10-23",
  "status": "AVAILABLE",
  "qualityGrade": "Grade A",
  "expectedWithdrawalDate": "2025-12-31",
  "notes": "Test inventory entry"
}
```

### ✅ Update Inventory
```
PUT http://localhost:8080/api/inventories/1
Content-Type: application/json

{
  "inventoryCode": "INV-TEST-001",
  "farmer": {
    "id": 2
  },
  "cropType": {
    "id": 1
  },
  "warehouse": {
    "id": 1
  },
  "storekeeper": {
    "id": 5
  },
  "quantityKg": 400.00,
  "pricePerKg": 900.00,
  "storageDate": "2025-10-23",
  "status": "RESERVED",
  "qualityGrade": "Grade A+",
  "expectedWithdrawalDate": "2025-11-30",
  "notes": "Updated inventory"
}
```

### ✅ Delete Inventory
```
DELETE http://localhost:8080/api/inventories/1
```

---

# 6️⃣ TRANSACTION APIs

### ✅ Get All Transactions
```
GET http://localhost:8080/api/transactions
```

### ✅ Get Transaction by ID
```
GET http://localhost:8080/api/transactions/1
```

### ✅ Get Transactions by Buyer
```
GET http://localhost:8080/api/transactions/buyer/7
```

### ✅ Get Transactions by Seller
```
GET http://localhost:8080/api/transactions/seller/2
```

### ✅ Get Transactions by Payment Status
```
GET http://localhost:8080/api/transactions/payment-status/COMPLETED
```
**Payment Statuses:** PENDING, COMPLETED, FAILED, REFUNDED

### ✅ Get Transactions by Delivery Status
```
GET http://localhost:8080/api/transactions/delivery-status/DELIVERED
```
**Delivery Statuses:** PENDING, IN_TRANSIT, DELIVERED, CANCELLED

### ✅ Get Total Sales by Seller
```
GET http://localhost:8080/api/transactions/seller/2/total-sales
```

### ✅ Get Transaction Count by Buyer
```
GET http://localhost:8080/api/transactions/buyer/7/count
```

### ✅ Get Transaction Count by Seller
```
GET http://localhost:8080/api/transactions/seller/2/count
```

### ✅ Create New Transaction
```
POST http://localhost:8080/api/transactions
Content-Type: application/json

{
  "transactionCode": "TRX-TEST-001",
  "buyer": {
    "id": 7
  },
  "seller": {
    "id": 2
  },
  "inventory": {
    "id": 1
  },
  "quantityKg": 100.00,
  "pricePerKg": 850.00,
  "totalAmount": 85000.00,
  "transactionDate": "2025-10-23T14:30:00",
  "paymentStatus": "PENDING",
  "deliveryStatus": "PENDING",
  "notes": "Test transaction"
}
```

### ✅ Update Transaction
```
PUT http://localhost:8080/api/transactions/1
Content-Type: application/json

{
  "transactionCode": "TRX-TEST-001",
  "buyer": {
    "id": 7
  },
  "seller": {
    "id": 2
  },
  "inventory": {
    "id": 1
  },
  "quantityKg": 100.00,
  "pricePerKg": 850.00,
  "totalAmount": 85000.00,
  "transactionDate": "2025-10-23T14:30:00",
  "paymentStatus": "COMPLETED",
  "deliveryStatus": "DELIVERED",
  "notes": "Transaction completed and delivered"
}
```

### ✅ Delete Transaction
```
DELETE http://localhost:8080/api/transactions/1
```

---

# 7️⃣ RATING APIs

### ✅ Get All Ratings
```
GET http://localhost:8080/api/ratings
```

### ✅ Get Rating by ID
```
GET http://localhost:8080/api/ratings/1
```

### ✅ Get Ratings Given by User
```
GET http://localhost:8080/api/ratings/rater/7
```

### ✅ Get Ratings Received by User
```
GET http://localhost:8080/api/ratings/ratee/2
```

### ✅ Get Ratings by Type
```
GET http://localhost:8080/api/ratings/type/QUALITY
```
**Rating Types:** QUALITY, RELIABILITY, PAYMENT, COMMUNICATION

### ✅ Get Average Rating for User
```
GET http://localhost:8080/api/ratings/user/2/average
```

### ✅ Create New Rating
```
POST http://localhost:8080/api/ratings
Content-Type: application/json

{
  "rater": {
    "id": 7
  },
  "ratee": {
    "id": 2
  },
  "transaction": {
    "id": 1
  },
  "ratingType": "QUALITY",
  "ratingScore": 5,
  "comment": "Excellent quality maize!"
}
```

**Rating Score:** 1-5 (integer)

### ✅ Update Rating
```
PUT http://localhost:8080/api/ratings/1
Content-Type: application/json

{
  "rater": {
    "id": 7
  },
  "ratee": {
    "id": 2
  },
  "transaction": {
    "id": 1
  },
  "ratingType": "QUALITY",
  "ratingScore": 4,
  "comment": "Good quality, updated review"
}
```

### ✅ Delete Rating
```
DELETE http://localhost:8080/api/ratings/1
```

---

# 8️⃣ WAREHOUSE ACCESS APIs

### ✅ Get All Warehouse Accesses
```
GET http://localhost:8080/api/warehouse-accesses
```

### ✅ Get Warehouse Access by ID
```
GET http://localhost:8080/api/warehouse-accesses/1
```

### ✅ Get Warehouse Accesses by User
```
GET http://localhost:8080/api/warehouse-accesses/user/5
```

### ✅ Get Warehouse Accesses by Warehouse
```
GET http://localhost:8080/api/warehouse-accesses/warehouse/1
```

### ✅ Get Warehouse Accesses by Access Level
```
GET http://localhost:8080/api/warehouse-accesses/access-level/FULL
```
**Access Levels:** READ_ONLY, LIMITED, FULL

### ✅ Get Active Warehouse Accesses
```
GET http://localhost:8080/api/warehouse-accesses/active
```

### ✅ Get Active Warehouse Accesses for User
```
GET http://localhost:8080/api/warehouse-accesses/user/5/active
```

### ✅ Create New Warehouse Access
```
POST http://localhost:8080/api/warehouse-accesses
Content-Type: application/json

{
  "user": {
    "id": 5
  },
  "warehouse": {
    "id": 1
  },
  "accessLevel": "FULL",
  "grantedDate": "2025-10-23",
  "expiryDate": "2026-10-23"
}
```

### ✅ Update Warehouse Access
```
PUT http://localhost:8080/api/warehouse-accesses/1
Content-Type: application/json

{
  "user": {
    "id": 5
  },
  "warehouse": {
    "id": 1
  },
  "accessLevel": "LIMITED",
  "grantedDate": "2025-10-23",
  "expiryDate": "2025-12-31"
}
```

### ✅ Delete Warehouse Access
```
DELETE http://localhost:8080/api/warehouse-accesses/1
```

---

# 📊 Testing Workflow Suggestions

## Scenario 1: Complete User Journey - Farmer Perspective
1. ✅ Get all provinces → `GET /api/provinces`
2. ✅ Get users by province → `GET /api/users/by-province-code/NOR`
3. ✅ Get user details → `GET /api/users/2`
4. ✅ Get user's complete location → `GET /api/users/2/location`
5. ✅ Get farmer's inventories → `GET /api/inventories/farmer/2`
6. ✅ Get farmer's total quantity → `GET /api/inventories/farmer/2/total-quantity`
7. ✅ Get farmer's sales → `GET /api/transactions/seller/2`
8. ✅ Get farmer's ratings → `GET /api/ratings/ratee/2`
9. ✅ Get farmer's average rating → `GET /api/ratings/user/2/average`

## Scenario 2: Complete User Journey - Buyer Perspective
1. ✅ Get all crop types → `GET /api/crop-types`
2. ✅ Get available inventories → `GET /api/inventories/available`
3. ✅ Get specific crop inventories → `GET /api/inventories/crop-type/1`
4. ✅ Create transaction → `POST /api/transactions`
5. ✅ Create rating → `POST /api/ratings`
6. ✅ Get buyer's purchases → `GET /api/transactions/buyer/7`

## Scenario 3: Warehouse Management
1. ✅ Get all warehouses → `GET /api/warehouses`
2. ✅ Get warehouses with capacity → `GET /api/warehouses/available-capacity`
3. ✅ Get total storage capacity → `GET /api/warehouses/total-capacity`
4. ✅ Get warehouse accesses → `GET /api/warehouse-accesses/warehouse/1`
5. ✅ Create warehouse access → `POST /api/warehouse-accesses`
6. ✅ Get active accesses → `GET /api/warehouse-accesses/active`

---

# 🎯 Quick Postman Setup Checklist

### For GET Requests:
1. ✅ Select **GET** from dropdown
2. ✅ Enter URL
3. ✅ Click **Send**

### For POST/PUT Requests:
1. ✅ Select **POST** or **PUT** from dropdown
2. ✅ Enter URL
3. ✅ Go to **Headers** tab
4. ✅ Add: `Content-Type` = `application/json`
5. ✅ Go to **Body** tab
6. ✅ Select **raw** and **JSON**
7. ✅ Paste JSON data
8. ✅ Click **Send**

### For DELETE Requests:
1. ✅ Select **DELETE** from dropdown
2. ✅ Enter URL with ID
3. ✅ Click **Send**

---

# 🔥 Pro Tips

1. **Use Swagger UI** for easier testing: `http://localhost:8080/swagger-ui.html`
2. **Start with GET requests** to see seeded data
3. **Note down IDs** from GET responses to use in other requests
4. **Test relationships** (e.g., create inventory → create transaction → create rating)
5. **Check validations** by sending incomplete/invalid data
6. **Test pagination** on list endpoints
7. **Test search/filter** endpoints with different parameters

---

# ✅ All Endpoints Summary

| Entity | Total Endpoints |
|--------|----------------|
| Provinces | 5 |
| Users | 12 |
| Crop Types | 6 |
| Warehouses | 9 |
| Inventories | 11 |
| Transactions | 11 |
| Ratings | 8 |
| Warehouse Accesses | 10 |
| **TOTAL** | **72 Endpoints** |

---

**Happy Testing! 🚀**

