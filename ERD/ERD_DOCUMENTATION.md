# 🗂️ Rangira Agro Farming - Entity Relationship Diagram

---

## 📊 **Database Overview**

### **Core Entities:**
1. **Location** (Hierarchical)
2. **User**
3. **User Profile**
4. **Crop Type**
5. **Storage Warehouse**
6. **Inventory**
7. **Transaction**
8. **Rating**
9. **Warehouse Access** (Junction Table)

---

## 🏗️ **Entity Relationships**

### **1. Location (Self-Referencing Hierarchy)**
```
Province
  └── District
        └── Sector
              └── Cell
                    └── Village
```

**Relationships:**
- **Location** ← (Many) **User**
- **Location** ← (Many) **Storage Warehouse**
- **Location** ← (Many) **Location** (Self-referencing)

---

### **2. User & User Profile**
```
User (1) ←→ (1) User Profile
```

**User Relationships:**
- **User** → (1) **Location**
- **User** ↔ (Many) **Warehouse Access** ↔ (Many) **Warehouse**
- **User** (as Farmer) ← (Many) **Inventory**
- **User** (as Storekeeper) ← (Many) **Inventory**
- **User** (as Buyer) ← (Many) **Transaction**
- **User** (as Seller) ← (Many) **Transaction**
- **User** (as Rater) ← (Many) **Rating**
- **User** (as Rated) ← (Many) **Rating**

---

### **3. Inventory Flow**
```
Farmer (User) → Inventory ← Crop Type
                    ↓
              Warehouse
                    ↓
            Storekeeper (User)
                    ↓
              Transaction
```

---

### **4. Transaction Flow**
```
Buyer (User) ← Transaction → Seller (User)
                    ↓
              Inventory
                    ↓
                Rating
```

## 📋 **Table Details**
### **1. Location**
**Purpose:** Hierarchical administrative divisions of Rwanda  
**Levels:** Province → District → Sector → Cell → Village  
**Key Fields:**
- `code` - Unique location code
- `level` - PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
- `parent_id` - Self-referencing for hierarchy

---

### **2. User**
**Purpose:** System users (farmers, buyers, storekeepers, admins)  
**Key Fields:**
- `user_type` - FARMER, BUYER, STOREKEEPER, ADMIN
- `status` - ACTIVE, SUSPENDED, INACTIVE
- `location_id` - User's location

---

### **3. User Profile**
**Purpose:** Extended user information  
**Relationship:** One-to-One with User  
**Key Fields:**
- `verified` - Account verification status
- `average_rating` - Calculated from received ratings

---

### **4. Crop Type**
**Purpose:** Master data for crop types  
**Key Fields:**
- `category` - CEREALS, LEGUMES, TUBERS, VEGETABLES, FRUITS, OTHER
- `measurement_unit` - KG, TONS, BAGS

---

### **5. Storage Warehouse**
**Purpose:** Physical storage facilities  
**Key Fields:**
- `warehouse_type` - COOPERATIVE, PRIVATE, GOVERNMENT
- `total_capacity_kg` - Maximum storage capacity
- `available_capacity_kg` - Current available space

---

### **6. Warehouse Access** (Junction Table)
**Purpose:** Many-to-Many relationship between Users and Warehouses  
**Key Fields:**
- `access_level` - OWNER, MANAGER, VIEWER
- `is_active` - Access status

---

### **7. Inventory**
**Purpose:** Stored crops in warehouses  
**Key Fields:**
- `farmer_id` - Who produced the crop
- `storekeeper_id` - Who logged it
- `remaining_quantity_kg` - Available for sale
- `status` - STORED, PARTIALLY_SOLD, SOLD, WITHDRAWN

---

### **8. Transaction**
**Purpose:** Buy/sell transactions  
**Key Fields:**
- `buyer_id` - Purchasing user
- `seller_id` - Selling user (usually farmer)
- `payment_status` - PENDING, PAID, FAILED
- `delivery_status` - PENDING, DELIVERED, CANCELLED

---

### **9. Rating**
**Purpose:** Trust and quality rating system  
**Key Fields:**
- `rating_score` - 1 to 5
- `rating_type` - QUALITY, RELIABILITY, PAYMENT, COMMUNICATION

---

## 🔗 **Relationship Summary**

| Entity | Relationships |
|----------|--------------|
| **Location** | Self-referencing (parent/children), Users (1:M), Warehouses (1:M) |
| **User** | Location (M:1), UserProfile (1:1), Warehouses (M:M via WarehouseAccess), Inventories (1:M as farmer/storekeeper), Transactions (1:M as buyer/seller), Ratings (1:M as rater/rated) |
| **CropType** | Inventories (1:M) |
| **StorageWarehouse** | Location (M:1), Users (M:M via WarehouseAccess), Inventories (1:M) |
| **Inventory** | User-Farmer (M:1), User-Storekeeper (M:1), Warehouse (M:1), CropType (M:1), Transactions (1:M) |
| **Transaction** | Inventory (M:1), User-Buyer (M:1), User-Seller (M:1), Ratings (1:M) |
| **Rating** | User-Rater (M:1), User-Rated (M:1), Transaction (M:1) |

---

## 🎯 **Key Features**

### **1. Hierarchical Locations**
- Self-referencing structure
- Supports 5 levels of administrative divisions
- Cascading relationships

### **2. Multi-Role Users**
- Single user can be: Farmer, Buyer, Storekeeper, Admin
- Different relationships based on role

### **3. Flexible Warehouse Access**
- Many-to-Many with access levels
- Time-bound access (expiry dates)
- Active/inactive status

### **4. Complete Transaction Tracking**
- Links buyers, sellers, and inventory
- Tracks payments and delivery
- Supports ratings and reviews

### **5. Trust System**
- Users rate each other after transactions
- Multiple rating types (quality, reliability, etc.)
- Average rating calculated in user profile


## 📊 **Database Statistics**

- **Total Tables:** 9
- **Total Relationships:** 18
- **Self-Referencing:** 1 (Location)
- **One-to-One:** 1 (User ↔ UserProfile)
- **One-to-Many:** 14
- **Many-to-Many:** 1 (User ↔ Warehouse via WarehouseAccess)

