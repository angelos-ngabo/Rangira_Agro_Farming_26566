# 📘 Complete System Testing Guide
## Rangira Agro Farming - API Testing Workflow

This guide provides a complete workflow for testing all CRUD operations in the correct order.

---

## 🔗 Dependency Chain

```
1. Locations (independent)
2. Crop Types (independent)
3. Users (depends on: Locations)
4. Storage Warehouses (depends on: Locations)
5. User Profiles (auto-created with Users)
6. Warehouse Access (depends on: Users, Warehouses)
7. Inventories (depends on: Users, Warehouses, Crop Types)
8. Transactions (depends on: Inventories, Users)
9. Ratings (depends on: Transactions, Users)
```

**✅ Data already seeded on startup:**
- Locations (Rwandan provinces, districts, sectors, cells, villages)
- Crop Types (Maize, Beans, Rice, etc.)
- Sample Users (5 users)
- Sample Warehouses (3 warehouses)

---

## 📋 STEP-BY-STEP TESTING WORKFLOW

### **PHASE 1: Verify Seeded Data**

#### 1.1 GET All Locations
```
GET http://localhost:8080/api/locations
```
Expected: List of Rwandan locations (provinces, districts, sectors, cells, villages)

#### 1.2 GET Locations by Level
```
GET http://localhost:8080/api/locations/level/PROVINCE
GET http://localhost:8080/api/locations/level/DISTRICT
GET http://localhost:8080/api/locations/level/VILLAGE
```

#### 1.3 GET All Crop Types
```
GET http://localhost:8080/api/crop-types
```
Expected: 8 crop types (Maize, Beans, Rice, Cassava, Irish Potato, Sweet Potato, Banana, Coffee)

#### 1.4 GET All Users
```
GET http://localhost:8080/api/users
```
Expected: 5 users (2 Farmers, 1 Buyer, 1 Storekeeper, 1 Admin)

#### 1.5 GET All Warehouses
```
GET http://localhost:8080/api/warehouses
```
Expected: 3 warehouses (Kigali Central, Musanze Storage, Huye Cooperative)

---

### **PHASE 2: Create Additional Data**

#### 2.1 Create New Location (Optional)
**POST** `http://localhost:8080/api/locations`

```json
{
  "code": "KG-GS-RW-NY-TEST",
  "name": "Test Village",
  "level": "VILLAGE",
  "parent": {
    "id": 15
  }
}
```

#### 2.2 Create New Crop Type (Optional)
**POST** `http://localhost:8080/api/crop-types`

```json

```

#### 2.3 Create New Users
**POST** `http://localhost:8080/api/users`

**Farmer #3:**
```json
{
  "userCode": "USR-F003",
  "firstName": "Marie",
  "lastName": "Uwase",
  "email": "marie.uwase@gmail.com",
  "phoneNumber": "+250788333333",
  "password": "password123",
  "userType": "FARMER",
  "locationId": 26
}
```

**Buyer #2:**
```json
{
  "userCode": "USR-B002",
  "firstName": "Patrick",
  "lastName": "Habimana",
  "email": "patrick.h@gmail.com",
  "phoneNumber": "+250788444444",
  "password": "password123",
  "userType": "BUYER",
  "locationId": 21
}
```

**Storekeeper #2:**
```json
{
  "userCode": "USR-S002",
  "firstName": "Alice",
  "lastName": "Mukamana",
  "email": "alice.m@gmail.com",
  "phoneNumber": "+250788555555",
  "password": "password123",
  "userType": "STOREKEEPER",
  "locationId": 16
}
```

#### 2.4 Create New Warehouse
**POST** `http://localhost:8080/api/warehouses`

```json
{
  "warehouseCode": "WH-004",
  "warehouseName": "Rubavu Storage Facility",
  "warehouseType": "GOVERNMENT",
  "totalCapacityKg": 150000.00,
  "availableCapacityKg": 150000.00,
  "locationId": 11
}
```

#### 2.5 Test User GET Endpoints
```
GET http://localhost:8080/api/users
GET http://localhost:8080/api/users/1
GET http://localhost:8080/api/users/location/code/KG-GS-RW
GET http://localhost:8080/api/users/type/FARMER
GET http://localhost:8080/api/users/email/jean.farmer@example.com
```

#### 2.6 Test Warehouse GET Endpoints
```
GET http://localhost:8080/api/warehouses
GET http://localhost:8080/api/warehouses/1
GET http://localhost:8080/api/warehouses/code/WH-001
GET http://localhost:8080/api/warehouses/type/COOPERATIVE
```

---

### **PHASE 3: Warehouse Access Management**

#### 3.1 Grant Warehouse Access to Users
**POST** `http://localhost:8080/api/warehouse-access`

**Grant Manager Access:**
```json
{
  "userId": 3,
  "warehouseId": 1,
  "accessLevel": "MANAGER",
  "grantedDate": "2025-10-01",
  "expiryDate": "2026-10-01",
  "isActive": true
}
```

**Grant Viewer Access:**
```json
{
  "userId": 2,
  "warehouseId": 2,
  "accessLevel": "VIEWER",
  "grantedDate": "2025-10-15",
  "isActive": true
}
```

**Grant Owner Access:**
```json
{
  "userId": 5,
  "warehouseId": 1,
  "accessLevel": "OWNER",
  "grantedDate": "2025-09-01",
  "isActive": true
}
```

#### 3.2 Test Warehouse Access GET Endpoints
```
GET http://localhost:8080/api/warehouse-access
GET http://localhost:8080/api/warehouse-access/1
GET http://localhost:8080/api/warehouse-access/user/3
GET http://localhost:8080/api/warehouse-access/warehouse/1
GET http://localhost:8080/api/warehouse-access/user/3/warehouse/1
```

---

### **PHASE 4: Inventory Management** ⚠️ **CRITICAL STEP**

#### 4.1 Create Inventories
**POST** `http://localhost:8080/api/inventories`

**Inventory #1 - Maize from Farmer 1:**
```json
{
  "inventoryCode": "INV-2025-001",
  "farmerId": 1,
  "warehouseId": 1,
  "cropTypeId": 1,
  "storekeeperId": 3,
  "quantityKg": 5000.00,
  "qualityGrade": "A",
  "storageDate": "2025-10-20",
  "expectedWithdrawalDate": "2026-01-20",
  "notes": "High quality maize from Gasabo district"
}
```

**Inventory #2 - Beans from Farmer 2:**
```json
{
  "inventoryCode": "INV-2025-002",
  "farmerId": 2,
  "warehouseId": 2,
  "cropTypeId": 2,
  "storekeeperId": 3,
  "quantityKg": 3000.00,
  "qualityGrade": "A",
  "storageDate": "2025-10-22",
  "expectedWithdrawalDate": "2025-12-22",
  "notes": "Organic red beans"
}
```

**Inventory #3 - Rice:**
```json
{
  "inventoryCode": "INV-2025-003",
  "farmerId": 1,
  "warehouseId": 3,
  "cropTypeId": 3,
  "storekeeperId": 3,
  "quantityKg": 2000.00,
  "qualityGrade": "B",
  "storageDate": "2025-10-25",
  "expectedWithdrawalDate": "2025-11-30",
  "notes": "Rice from Huye cooperative"
}
```

**Inventory #4 - Cassava:**
```json
{
  "inventoryCode": "INV-2025-004",
  "farmerId": 2,
  "warehouseId": 1,
  "cropTypeId": 4,
  "storekeeperId": 3,
  "quantityKg": 4000.00,
  "qualityGrade": "A",
  "storageDate": "2025-10-27",
  "expectedWithdrawalDate": "2025-12-01",
  "notes": "Fresh cassava harvest"
}
```

**Inventory #5 - Irish Potato:**
```json
{
  "inventoryCode": "INV-2025-005",
  "farmerId": 1,
  "warehouseId": 2,
  "cropTypeId": 5,
  "storekeeperId": 3,
  "quantityKg": 6000.00,
  "qualityGrade": "A",
  "storageDate": "2025-10-28",
  "expectedWithdrawalDate": "2026-02-28",
  "notes": "Premium Irish potatoes from Musanze"
}
```

#### 4.2 Test Inventory GET Endpoints
```
GET http://localhost:8080/api/inventories
GET http://localhost:8080/api/inventories/1
GET http://localhost:8080/api/inventories/code/INV-2025-001
GET http://localhost:8080/api/inventories/farmer/1
GET http://localhost:8080/api/inventories/warehouse/1
GET http://localhost:8080/api/inventories/crop-type/1
GET http://localhost:8080/api/inventories/status/STORED
```

---

### **PHASE 5: Transaction Management** ⚠️ **Requires Inventories**

#### 5.1 Create Transactions
**POST** `http://localhost:8080/api/transactions`

**Transaction #1 - Buyer 1 purchases Maize from Farmer 1:**
```json
{
  "transactionCode": "TXN-2025-001",
  "inventoryId": 1,
  "buyerId": 4,
  "sellerId": 1,
  "quantityKg": 1000.00,
  "unitPrice": 800.00,
  "totalAmount": 800000.00,
  "storageFee": 10000.00,
  "transactionFee": 8000.00,
  "netAmount": 782000.00,
  "paymentStatus": "PAID",
  "deliveryStatus": "DELIVERED",
  "transactionDate": "2025-10-25T10:30:00",
  "paymentDate": "2025-10-25T15:00:00",
  "notes": "First maize sale - delivered to Kigali market"
}
```

**Transaction #2 - Buyer 1 purchases Beans:**
```json
{
  "transactionCode": "TXN-2025-002",
  "inventoryId": 2,
  "buyerId": 4,
  "sellerId": 2,
  "quantityKg": 500.00,
  "unitPrice": 1200.00,
  "totalAmount": 600000.00,
  "storageFee": 5000.00,
  "transactionFee": 6000.00,
  "netAmount": 589000.00,
  "paymentStatus": "PAID",
  "deliveryStatus": "DELIVERED",
  "transactionDate": "2025-10-26T09:00:00",
  "paymentDate": "2025-10-26T14:00:00",
  "notes": "Organic beans purchase"
}
```

**Transaction #3 - Pending Payment:**
```json
{
  "transactionCode": "TXN-2025-003",
  "inventoryId": 3,
  "buyerId": 4,
  "sellerId": 1,
  "quantityKg": 800.00,
  "unitPrice": 900.00,
  "totalAmount": 720000.00,
  "storageFee": 8000.00,
  "transactionFee": 7200.00,
  "netAmount": 704800.00,
  "paymentStatus": "PENDING",
  "deliveryStatus": "PENDING",
  "transactionDate": "2025-10-28T11:00:00",
  "notes": "Rice order - awaiting payment"
}
```

**Transaction #4 - Cassava Sale:**
```json
{
  "transactionCode": "TXN-2025-004",
  "inventoryId": 4,
  "buyerId": 4,
  "sellerId": 2,
  "quantityKg": 2000.00,
  "unitPrice": 600.00,
  "totalAmount": 1200000.00,
  "storageFee": 15000.00,
  "transactionFee": 12000.00,
  "netAmount": 1173000.00,
  "paymentStatus": "PAID",
  "deliveryStatus": "DELIVERED",
  "transactionDate": "2025-10-28T14:30:00",
  "paymentDate": "2025-10-29T10:00:00",
  "notes": "Bulk cassava order"
}
```

**Transaction #5 - Irish Potato:**
```json
{
  "transactionCode": "TXN-2025-005",
  "inventoryId": 5,
  "buyerId": 4,
  "sellerId": 1,
  "quantityKg": 1500.00,
  "unitPrice": 700.00,
  "totalAmount": 1050000.00,
  "storageFee": 12000.00,
  "transactionFee": 10500.00,
  "netAmount": 1027500.00,
  "paymentStatus": "PAID",
  "deliveryStatus": "PENDING",
  "transactionDate": "2025-10-29T08:00:00",
  "paymentDate": "2025-10-29T12:00:00",
  "notes": "Premium potatoes - pending delivery"
}
```

#### 5.2 Test Transaction GET Endpoints
```
GET http://localhost:8080/api/transactions
GET http://localhost:8080/api/transactions/1
GET http://localhost:8080/api/transactions/code/TXN-2025-001
GET http://localhost:8080/api/transactions/buyer/4
GET http://localhost:8080/api/transactions/seller/1
GET http://localhost:8080/api/transactions/inventory/1
GET http://localhost:8080/api/transactions/payment-status/PAID
GET http://localhost:8080/api/transactions/delivery-status/DELIVERED
```

---

### **PHASE 6: Rating System** ⚠️ **Requires Transactions**

#### 6.1 Create Ratings
**POST** `http://localhost:8080/api/ratings`

**Rating #1 - Buyer rates Farmer 1 (Quality):**
```json
{
  "raterId": 4,
  "ratedUserId": 1,
  "transactionId": 1,
  "ratingScore": 5,
  "ratingType": "QUALITY",
  "comment": "Excellent quality maize! Very satisfied with the product."
}
```

**Rating #2 - Buyer rates Farmer 1 (Reliability):**
```json
{
  "raterId": 4,
  "ratedUserId": 1,
  "transactionId": 1,
  "ratingScore": 5,
  "ratingType": "RELIABILITY",
  "comment": "Very reliable farmer. Delivered on time."
}
```

**Rating #3 - Farmer 1 rates Buyer (Payment):**
```json
{
  "raterId": 1,
  "ratedUserId": 4,
  "transactionId": 1,
  "ratingScore": 5,
  "ratingType": "PAYMENT",
  "comment": "Quick payment. Highly recommended buyer."
}
```

**Rating #4 - Buyer rates Farmer 2 (Quality):**
```json
{
  "raterId": 4,
  "ratedUserId": 2,
  "transactionId": 2,
  "ratingScore": 4,
  "ratingType": "QUALITY",
  "comment": "Good quality beans. Would buy again."
}
```

**Rating #5 - Buyer rates Farmer 2 (Communication):**
```json
{
  "raterId": 4,
  "ratedUserId": 2,
  "transactionId": 2,
  "ratingScore": 5,
  "ratingType": "COMMUNICATION",
  "comment": "Excellent communication throughout the process."
}
```

**Rating #6 - Farmer 2 rates Buyer (Payment):**
```json
{
  "raterId": 2,
  "ratedUserId": 4,
  "transactionId": 2,
  "ratingScore": 5,
  "ratingType": "PAYMENT",
  "comment": "Prompt payment. Great to work with."
}
```

**Rating #7 - Transaction 4 - Quality:**
```json
{
  "raterId": 4,
  "ratedUserId": 2,
  "transactionId": 4,
  "ratingScore": 5,
  "ratingType": "QUALITY",
  "comment": "Best cassava I've purchased. A+ quality!"
}
```

**Rating #8 - Transaction 5 - Quality:**
```json
{
  "raterId": 4,
  "ratedUserId": 1,
  "transactionId": 5,
  "ratingScore": 5,
  "ratingType": "QUALITY",
  "comment": "Premium Irish potatoes. Exactly as described."
}
```

#### 6.2 Test Rating GET Endpoints
```
GET http://localhost:8080/api/ratings
GET http://localhost:8080/api/ratings/1
GET http://localhost:8080/api/ratings/rater/4
GET http://localhost:8080/api/ratings/rated-user/1
GET http://localhost:8080/api/ratings/transaction/1
GET http://localhost:8080/api/ratings/type/QUALITY
```

---

### **PHASE 7: Update Operations (PUT)**

#### 7.1 Update User
**PUT** `http://localhost:8080/api/users/1`
```json
{
  "userCode": "USR-F001",
  "firstName": "Jean Baptiste",
  "lastName": "Niyonzima",
  "email": "jean.farmer@example.com",
  "phoneNumber": "+250788111111",
  "password": "newpassword123",
  "userType": "FARMER",
  "status": "ACTIVE",
  "locationId": 26
}
```

#### 7.2 Update User Profile
**PUT** `http://localhost:8080/api/user-profiles/user/1`
```json
{
  "nationalId": "1198780012345678",
  "dateOfBirth": "1988-05-15",
  "gender": "MALE",
  "bio": "Experienced farmer specializing in maize and rice cultivation. 10+ years experience.",
  "verified": true
}
```

#### 7.3 Update Warehouse
**PUT** `http://localhost:8080/api/warehouses/1`
```json
{
  "warehouseCode": "WH-001",
  "warehouseName": "Kigali Central Storage - Updated",
  "warehouseType": "COOPERATIVE",
  "totalCapacityKg": 120000.00,
  "availableCapacityKg": 85000.00,
  "status": "ACTIVE",
  "locationId": 16
}
```

#### 7.4 Update Inventory Status
**PUT** `http://localhost:8080/api/inventories/1`
```json
{
  "inventoryCode": "INV-2025-001",
  "farmerId": 1,
  "warehouseId": 1,
  "cropTypeId": 1,
  "storekeeperId": 3,
  "quantityKg": 5000.00,
  "remainingQuantityKg": 4000.00,
  "qualityGrade": "A",
  "storageDate": "2025-10-20",
  "expectedWithdrawalDate": "2026-01-20",
  "status": "PARTIALLY_SOLD",
  "notes": "1000 kg sold to Buyer 1"
}
```

#### 7.5 Update Transaction Status
**PUT** `http://localhost:8080/api/transactions/3`
```json
{
  "transactionCode": "TXN-2025-003",
  "inventoryId": 3,
  "buyerId": 4,
  "sellerId": 1,
  "quantityKg": 800.00,
  "unitPrice": 900.00,
  "totalAmount": 720000.00,
  "storageFee": 8000.00,
  "transactionFee": 7200.00,
  "netAmount": 704800.00,
  "paymentStatus": "PAID",
  "deliveryStatus": "DELIVERED",
  "transactionDate": "2025-10-28T11:00:00",
  "paymentDate": "2025-10-29T16:00:00",
  "notes": "Payment received and delivered"
}
```

---

### **PHASE 8: Advanced Queries**

#### 8.1 Pagination
```
GET http://localhost:8080/api/users/paginated?page=0&size=5
GET http://localhost:8080/api/inventories/paginated?page=0&size=10
GET http://localhost:8080/api/transactions/paginated?page=0&size=5
```

#### 8.2 Search
```
GET http://localhost:8080/api/users/search?keyword=jean
GET http://localhost:8080/api/warehouses/search?name=kigali
```

#### 8.3 Statistics
```
GET http://localhost:8080/api/users/count
GET http://localhost:8080/api/inventories/count
GET http://localhost:8080/api/transactions/count
GET http://localhost:8080/api/locations/count
```

---

### **PHASE 9: Delete Operations (DELETE)** ⚠️ Use with caution!

**Delete in reverse dependency order:**

#### 9.1 Delete Rating
```
DELETE http://localhost:8080/api/ratings/1
```

#### 9.2 Delete Transaction
```
DELETE http://localhost:8080/api/transactions/1
```

#### 9.3 Delete Warehouse Access
```
DELETE http://localhost:8080/api/warehouse-access/1
```

#### 9.4 Delete Inventory
```
DELETE http://localhost:8080/api/inventories/1
```

#### 9.5 Delete User (will also delete UserProfile)
```
DELETE http://localhost:8080/api/users/6
```

#### 9.6 Delete Warehouse
```
DELETE http://localhost:8080/api/warehouses/4
```

---

## 🎯 Quick Testing Checklist

### ✅ Locations
- [x] GET all locations
- [x] GET location by ID
- [x] GET location by code
- [x] GET locations by level
- [x] GET children of location
- [x] POST new location
- [x] PUT update location
- [x] DELETE location

### ✅ Crop Types
- [x] GET all crop types
- [x] GET crop type by ID
- [x] GET crop type by code
- [x] GET crop types by category
- [x] POST new crop type
- [x] PUT update crop type
- [x] DELETE crop type

### ✅ Users
- [x] GET all users
- [x] GET user by ID
- [x] GET user by code
- [x] GET user by email
- [x] GET users by type
- [x] GET users by location
- [x] GET users by status
- [x] POST new user
- [x] PUT update user
- [x] DELETE user

### ✅ User Profiles
- [x] GET all user profiles
- [x] GET user profile by ID
- [x] GET user profile by user ID
- [x] GET verified users
- [x] PUT update user profile

### ✅ Warehouses
- [x] GET all warehouses
- [x] GET warehouse by ID
- [x] GET warehouse by code
- [x] GET warehouses by type
- [x] GET warehouses by status
- [x] POST new warehouse
- [x] PUT update warehouse
- [x] DELETE warehouse

### ✅ Warehouse Access
- [x] GET all warehouse access
- [x] GET warehouse access by ID
- [x] GET access by user
- [x] GET access by warehouse
- [x] POST grant access
- [x] PUT update access
- [x] DELETE revoke access

### ✅ Inventories
- [x] GET all inventories
- [x] GET inventory by ID
- [x] GET inventory by code
- [x] GET inventories by farmer
- [x] GET inventories by warehouse
- [x] GET inventories by crop type
- [x] GET inventories by status
- [x] POST new inventory
- [x] PUT update inventory
- [x] DELETE inventory

### ✅ Transactions
- [x] GET all transactions
- [x] GET transaction by ID
- [x] GET transaction by code
- [x] GET transactions by buyer
- [x] GET transactions by seller
- [x] GET transactions by inventory
- [x] GET transactions by payment status
- [x] GET transactions by delivery status
- [x] POST new transaction
- [x] PUT update transaction
- [x] DELETE transaction

### ✅ Ratings
- [x] GET all ratings
- [x] GET rating by ID
- [x] GET ratings by rater
- [x] GET ratings by rated user
- [x] GET ratings by transaction
- [x] GET ratings by type
- [x] POST new rating
- [x] PUT update rating
- [x] DELETE rating

---

## 📊 Sample Response Verification

After creating all data, verify counts:
- **Locations**: ~29 (seeded)
- **Crop Types**: ~8-10
- **Users**: ~5-8
- **Warehouses**: ~3-4
- **Warehouse Access**: ~3
- **Inventories**: ~5
- **Transactions**: ~5
- **Ratings**: ~8

---

## 🔥 Common Errors & Solutions

### Error: "Transaction not found"
**Solution**: Create inventories first, then transactions

### Error: "Inventory not found"
**Solution**: Verify inventory exists before creating transaction

### Error: "User not found"
**Solution**: Check user ID exists in database

### Error: "Location not found"
**Solution**: Use location ID from GET /api/locations

### Error: "Duplicate key violation"
**Solution**: Use unique codes (userCode, inventoryCode, transactionCode, etc.)

---

## 🚀 Quick Start Command

Start the application:
```bash
mvn spring-boot:run
```

Access Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

---

**Created by: AI Assistant**  
**Last Updated: October 29, 2025**

