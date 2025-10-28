# 🌱 Rangira Agro Farming - Entity Relationship Diagram (ERD)

## 📋 Project Overview

This ERD supports a comprehensive agricultural management system that digitizes crop storage, connects farmers to buyers, and builds trust through verified inventories and ratings.

---

## 🎯 Requirements Checklist

### ✅ Technical Requirements Met:

1. **At least 5 Entities**: ✅ **12 Entities** (exceeds requirement)
2. **Complete CRUD Operations**: ✅ All entities will have full CRUD
3. **JPA Repository Methods**: ✅ Will include findBy, existsBy, sorting, pagination
4. **Rwandan Location Hierarchy**: ✅ Province → District → Sector → Cell → Village
5. **User-Location Relationship**: ✅ User linked to Village
6. **All Three Relationship Types**:
   - ✅ **One-to-One**: User ↔ UserProfile
   - ✅ **One-to-Many/Many-to-One**: Multiple (location hierarchy, inventory, transactions)
   - ✅ **Many-to-Many**: User ↔ StorageWarehouse (via WarehouseAccess)

---

## 📊 Entity Details

### 1. **Location Entities** (Rwandan Administrative Structure)

| Entity | Description | Key Fields |
|--------|-------------|-----------|
| **Province** | Top-level administrative division | province_code, province_name |
| **District** | Second-level division | district_code, district_name, province_id |
| **Sector** | Third-level division | sector_code, sector_name, district_id |
| **Cell** | Fourth-level division | cell_code, cell_name, sector_id |
| **Village** | Lowest administrative level | village_code, village_name, cell_id |

**Relationship**: Province → District → Sector → Cell → Village (cascading one-to-many)

---

### 2. **User Management Entities**

#### **User**
- **Purpose**: Main user table for farmers, buyers, storekeepers, and admins
- **Key Fields**: 
  - user_code (unique identifier)
  - email, phone_number (authentication)
  - user_type (FARMER, BUYER, STOREKEEPER, ADMIN)
  - village_id (links to location)
- **Relationships**:
  - Many-to-One with Village (user location)
  - One-to-One with UserProfile
  - One-to-Many with Inventory (as farmer)
  - One-to-Many with Inventory (as storekeeper)
  - Many-to-Many with StorageWarehouse (via WarehouseAccess)

#### **UserProfile** 
- **Purpose**: Extended user information (ONE-TO-ONE relationship)
- **Key Fields**: 
  - national_id, date_of_birth, gender
  - verified (verification status)
  - average_rating (trust score)
- **Relationship**: One-to-One with User

---

### 3. **Storage Management Entities**

#### **StorageWarehouse**
- **Purpose**: Physical storage locations (cooperatives, private, government)
- **Key Fields**: 
  - warehouse_code, warehouse_name
  - total_capacity_kg, available_capacity_kg
  - village_id (warehouse location)
  - status (ACTIVE, MAINTENANCE, CLOSED)
- **Relationships**:
  - Many-to-One with Village
  - Many-to-Many with User (via WarehouseAccess)
  - One-to-Many with Inventory

#### **WarehouseAccess**
- **Purpose**: Junction table for User-Warehouse access control (MANY-TO-MANY)
- **Key Fields**: 
  - user_id, warehouse_id
  - access_level (OWNER, MANAGER, VIEWER)
  - granted_date, expiry_date
  - is_active
- **Relationships**: Links User and StorageWarehouse

---

### 4. **Crop & Inventory Entities**

#### **CropType**
- **Purpose**: Master data for crop types
- **Key Fields**: 
  - crop_code, crop_name
  - category (CEREALS, LEGUMES, TUBERS)
  - measurement_unit (KG, TONS, BAGS)
- **Relationship**: One-to-Many with Inventory

#### **Inventory**
- **Purpose**: Core entity tracking stored crops with verification
- **Key Fields**: 
  - inventory_code (unique)
  - farmer_id (who owns the crop)
  - warehouse_id (where it's stored)
  - crop_type_id (what crop)
  - storekeeper_id (who logged it)
  - quantity_kg, remaining_quantity_kg
  - quality_grade (A, B, C)
  - status (STORED, PARTIALLY_SOLD, SOLD, WITHDRAWN)
- **Relationships**:
  - Many-to-One with User (farmer)
  - Many-to-One with User (storekeeper)
  - Many-to-One with StorageWarehouse
  - Many-to-One with CropType
  - One-to-Many with Transaction

---

### 5. **Transaction Entity**

#### **Transaction**
- **Purpose**: Records all purchase/sale transactions
- **Key Fields**: 
  - transaction_code (unique)
  - inventory_id (what was sold)
  - buyer_id, seller_id (who participated)
  - quantity_kg, unit_price, total_amount
  - storage_fee, transaction_fee (revenue sources)
  - payment_status, delivery_status
- **Relationships**:
  - Many-to-One with Inventory
  - Many-to-One with User (buyer)
  - Many-to-One with User (seller)
  - One-to-Many with Rating

---

### 6. **Rating Entity**

#### **Rating**
- **Purpose**: Trust and rating system
- **Key Fields**: 
  - rater_id (who gave the rating)
  - rated_user_id (who received it)
  - transaction_id (context)
  - rating_score (1-5)
  - rating_type (QUALITY, RELIABILITY, PAYMENT, COMMUNICATION)
- **Relationships**:
  - Many-to-One with User (rater)
  - Many-to-One with User (rated user)
  - Many-to-One with Transaction

---

## 🔗 Relationship Summary

| Type | Example | Description |
|------|---------|-------------|
| **One-to-One** | User ↔ UserProfile | Each user has exactly one profile |
| **One-to-Many** | Province → District | One province has many districts |
| **One-to-Many** | Village → User | One village has many users |
| **One-to-Many** | Village → StorageWarehouse | One village has many warehouses |
| **One-to-Many** | StorageWarehouse → Inventory | One warehouse contains many inventory items |
| **One-to-Many** | User (Farmer) → Inventory | One farmer can store multiple crops |
| **One-to-Many** | Inventory → Transaction | One inventory can have multiple transactions |
| **Many-to-Many** | User ↔ StorageWarehouse | Users can access multiple warehouses; warehouses can have multiple authorized users |

---

## 🛠️ How to View the ERD

### Option 1: Mermaid (Recommended)
1. Open the file `ERD_MERMAID.md`
2. Copy the Mermaid code
3. Paste into [Mermaid Live Editor](https://mermaid.live/)
4. Or view directly in VS Code with "Markdown Preview Mermaid Support" extension
5. Or paste in GitHub/GitLab markdown files (auto-renders)

### Option 2: PlantUML
1. Open the file `ERD_PLANTUML.puml`
2. Use [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
3. Or use VS Code extension: "PlantUML"
4. Or use IntelliJ IDEA PlantUML plugin

### Option 3: dbdiagram.io (Most Visual)
1. Open the file `ERD_DBDIAGRAM.dbml`
2. Go to [https://dbdiagram.io/](https://dbdiagram.io/)
3. Click "Import" → Paste the code
4. View interactive, beautiful diagram with zoom/pan
5. Export as PDF or PNG for documentation

---

## 📝 Key Business Logic Supported

1. **Verified Storage**: Every inventory entry requires both farmer and storekeeper, ensuring accountability
2. **Location Tracking**: Full administrative hierarchy enables regional analytics and logistics
3. **Access Control**: WarehouseAccess table manages who can view/manage which warehouses
4. **Trust Building**: Rating system with average_rating in UserProfile builds reputation
5. **Transaction Transparency**: Complete audit trail from storage → sale → rating
6. **Capacity Management**: Warehouses track total vs. available capacity
7. **Quality Grading**: Crops graded on storage, giving buyers confidence

---

## 🚀 Next Steps

After ERD approval, we'll implement:

1. ✅ Spring Boot project structure
2. ✅ JPA Entity classes with annotations
3. ✅ Repository interfaces with custom query methods
4. ✅ Service layer with business logic
5. ✅ REST Controllers with CRUD operations
6. ✅ Data seeding for Rwandan locations
7. ✅ Validation and error handling
8. ✅ API documentation

---

## 📚 Implementation Notes

- **Soft Delete**: Consider adding `deleted_at` timestamp for soft deletes
- **Audit Trail**: `created_at` and `updated_at` on all entities
- **Indexing**: Key fields indexed for performance (user_code, warehouse_code, etc.)
- **Enums**: User types, statuses, etc. will be Java Enums in implementation
- **Validation**: Email, phone number formats validated
- **Security**: Passwords hashed (BCrypt), JWT for authentication

---

## 💡 Questions or Modifications?

This ERD is designed to be comprehensive yet flexible. If you need to:
- Add more entities
- Modify relationships
- Add specific fields for your use case

Let me know and we'll refine it before implementation!

---

**Created for**: Rangira Agro Farming - Web Technology Midterm Project  
**Date**: October 23, 2025  
**Tech Stack**: Spring Boot + JPA + PostgreSQL/MySQL

