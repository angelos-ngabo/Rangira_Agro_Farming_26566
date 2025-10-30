# Rangira Agro Farming - Entity Relationship Diagram

## Database Overview

### Core Entities

1. Location (Hierarchical)
2. User
3. User Profile
4. Crop Type
5. Storage Warehouse
6. Inventory
7. Transaction
8. Rating
9. Warehouse Access (Junction Table)

## Entity Relationships

### 1. Location (Self-Referencing Hierarchy)

```
Province
  └── District
        └── Sector
              └── Cell
                    └── Village
```

**Relationships:**
- Location ← (Many) User
- Location ← (Many) Storage Warehouse
- Location ← (Many) Location (Self-referencing)

### 2. User & User Profile

```
User (1) ←→ (1) User Profile
```

**User Relationships:**
- User → (1) Location
- User ↔ (Many) Warehouse Access ↔ (Many) Warehouse
- User (as Farmer) ← (Many) Inventory
- User (as Storekeeper) ← (Many) Inventory
- User (as Buyer) ← (Many) Transaction
- User (as Seller) ← (Many) Transaction
- User (as Rater) ← (Many) Rating
- User (as Rated User) ← (Many) Rating

### 3. Storage Warehouse

**Relationships:**
- Warehouse → (1) Location
- Warehouse ← (Many) Inventory
- Warehouse ↔ (Many) Warehouse Access ↔ (Many) User

### 4. Inventory

**Relationships:**
- Inventory → (1) Farmer (User)
- Inventory → (1) Storekeeper (User)
- Inventory → (1) Warehouse
- Inventory → (1) Crop Type
- Inventory ← (Many) Transaction

### 5. Transaction

**Relationships:**
- Transaction → (1) Inventory
- Transaction → (1) Seller (User)
- Transaction → (1) Buyer (User)
- Transaction ← (Many) Rating

### 6. Rating

**Relationships:**
- Rating → (1) Transaction
- Rating → (1) Rater (User)
- Rating → (1) Rated User (User)

## Relationship Types

### One-to-One (1:1)
- User ← → User Profile

### One-to-Many (1:N)
- Location → User
- Location → Warehouse
- Location → Location (parent-child)
- User (Farmer) → Inventory
- User (Storekeeper) → Inventory
- User (Seller) → Transaction
- User (Buyer) → Transaction
- User (Rater) → Rating
- User (Rated User) → Rating
- Warehouse → Inventory
- Crop Type → Inventory
- Inventory → Transaction
- Transaction → Rating

### Many-to-Many (M:N)
- User ↔ Warehouse Access ↔ Warehouse

## Key Attributes

### Location
- id (PK)
- code (Unique)
- name
- level (PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE)
- parent_id (FK, Self-referencing)

### User
- id (PK)
- user_code (Unique)
- email (Unique)
- phone_number (Unique)
- first_name
- last_name
- password
- user_type (ADMIN, FARMER, BUYER, STOREKEEPER)
- status (ACTIVE, INACTIVE, SUSPENDED)
- location_id (FK → Location)

### User Profile
- id (PK)
- user_id (FK → User, Unique)
- bio
- gender
- date_of_birth
- profile_image_url
- national_id
- address

### Storage Warehouse
- id (PK)
- warehouse_code (Unique)
- warehouse_name
- warehouse_type (PRIVATE, COOPERATIVE, GOVERNMENT)
- location_id (FK → Location)
- total_capacity_kg
- available_capacity_kg
- status (ACTIVE, INACTIVE, MAINTENANCE)

### Crop Type
- id (PK)
- crop_code (Unique)
- crop_name
- category (CEREALS, LEGUMES, TUBERS, VEGETABLES, FRUITS, OTHER)
- measurement_unit (TONS, BAGS, KG)
- description

### Inventory
- id (PK)
- inventory_code (Unique)
- farmer_id (FK → User)
- warehouse_id (FK → Storage Warehouse)
- crop_type_id (FK → Crop Type)
- storekeeper_id (FK → User)
- quantity_kg
- quality_grade
- storage_date
- expected_withdrawal_date
- status (STORED, WITHDRAWN, SOLD)

### Transaction
- id (PK)
- transaction_code (Unique)
- inventory_id (FK → Inventory)
- seller_id (FK → User)
- buyer_id (FK → User)
- quantity_kg
- unit_price
- total_amount
- commission_amount
- net_amount
- transaction_date
- payment_status (PENDING, COMPLETED, FAILED, REFUNDED)
- delivery_status (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)

### Rating
- id (PK)
- transaction_id (FK → Transaction)
- rater_id (FK → User)
- rated_user_id (FK → User)
- rating_type (QUALITY, RELIABILITY, COMMUNICATION, PAYMENT)
- rating_value (1-5)
- comment
- rating_date

### Warehouse Access
- id (PK)
- user_id (FK → User)
- warehouse_id (FK → Storage Warehouse)
- access_level (VIEWER, MANAGER, OWNER)
- granted_date
- expiry_date
- is_active

## Database Constraints

### Unique Constraints
- location.code
- user.user_code
- user.email
- user.phone_number
- user_profile.user_id
- storage_warehouse.warehouse_code
- crop_type.crop_code
- inventory.inventory_code
- transaction.transaction_code

### Foreign Key Constraints
- All relationships enforce referential integrity
- Cascade operations configured for dependent entities

### Check Constraints
- rating_value BETWEEN 1 AND 5
- quantity_kg > 0
- unit_price > 0
- available_capacity_kg <= total_capacity_kg

## Indexes

Recommended indexes for performance:
- location.code
- location.level
- user.email
- user.phone_number
- user.user_code
- user.user_type
- user.location_id
- warehouse.warehouse_code
- warehouse.location_id
- inventory.farmer_id
- inventory.warehouse_id
- transaction.seller_id
- transaction.buyer_id
- rating.rated_user_id
