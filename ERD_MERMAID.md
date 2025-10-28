# Rangira Agro Farming - Entity Relationship Diagram (Mermaid)

## How to View:
1. Copy the code below into any Mermaid Live Editor: https://mermaid.live/
2. Or use a VS Code extension: "Markdown Preview Mermaid Support"
3. Or paste in GitHub/GitLab markdown files

```mermaid
erDiagram
    %% LOCATION HIERARCHY (Rwandan Administrative Structure)
    PROVINCE {
        bigint id PK
        string province_code UK "Unique code"
        string province_name UK "e.g., Kigali, Northern"
        datetime created_at
        datetime updated_at
    }
    
    DISTRICT {
        bigint id PK
        string district_code UK
        string district_name
        bigint province_id FK
        datetime created_at
        datetime updated_at
    }
    
    SECTOR {
        bigint id PK
        string sector_code UK
        string sector_name
        bigint district_id FK
        datetime created_at
        datetime updated_at
    }
    
    CELL {
        bigint id PK
        string cell_code UK
        string cell_name
        bigint sector_id FK
        datetime created_at
        datetime updated_at
    }
    
    VILLAGE {
        bigint id PK
        string village_code UK
        string village_name
        bigint cell_id FK
        datetime created_at
        datetime updated_at
    }
    
    %% USER MANAGEMENT
    USER {
        bigint id PK
        string user_code UK "e.g., USR-2024-001"
        string first_name
        string last_name
        string email UK
        string phone_number UK
        string password
        enum user_type "FARMER, BUYER, STOREKEEPER, ADMIN"
        enum status "ACTIVE, SUSPENDED, INACTIVE"
        bigint village_id FK "User's location"
        datetime created_at
        datetime updated_at
    }
    
    USER_PROFILE {
        bigint id PK
        bigint user_id FK UK "One-to-One with User"
        string national_id
        date date_of_birth
        enum gender "MALE, FEMALE, OTHER"
        string profile_picture_url
        text bio
        boolean verified
        decimal average_rating
        datetime created_at
        datetime updated_at
    }
    
    %% STORAGE MANAGEMENT
    STORAGE_WAREHOUSE {
        bigint id PK
        string warehouse_code UK "e.g., WH-MUS-001"
        string warehouse_name
        string warehouse_type "COOPERATIVE, PRIVATE, GOVERNMENT"
        decimal total_capacity_kg
        decimal available_capacity_kg
        bigint village_id FK "Warehouse location"
        enum status "ACTIVE, MAINTENANCE, CLOSED"
        datetime created_at
        datetime updated_at
    }
    
    WAREHOUSE_ACCESS {
        bigint id PK
        bigint user_id FK
        bigint warehouse_id FK
        enum access_level "OWNER, MANAGER, VIEWER"
        date granted_date
        date expiry_date
        boolean is_active
        datetime created_at
        datetime updated_at
    }
    
    %% CROP & INVENTORY MANAGEMENT
    CROP_TYPE {
        bigint id PK
        string crop_code UK "e.g., BEANS-001"
        string crop_name "Beans, Maize, Rice"
        string category "CEREALS, LEGUMES, TUBERS"
        string measurement_unit "KG, TONS, BAGS"
        text description
        datetime created_at
        datetime updated_at
    }
    
    INVENTORY {
        bigint id PK
        string inventory_code UK "e.g., INV-2024-001"
        bigint farmer_id FK "User who stored the crop"
        bigint warehouse_id FK
        bigint crop_type_id FK
        bigint storekeeper_id FK "User who logged it"
        decimal quantity_kg
        decimal remaining_quantity_kg
        string quality_grade "A, B, C"
        date storage_date
        date expected_withdrawal_date
        enum status "STORED, PARTIALLY_SOLD, SOLD, WITHDRAWN"
        text notes
        datetime created_at
        datetime updated_at
    }
    
    %% TRANSACTION MANAGEMENT
    TRANSACTION {
        bigint id PK
        string transaction_code UK "e.g., TXN-2024-001"
        bigint inventory_id FK
        bigint buyer_id FK "User buying"
        bigint seller_id FK "User selling (farmer)"
        decimal quantity_kg
        decimal unit_price
        decimal total_amount
        decimal storage_fee
        decimal transaction_fee
        decimal net_amount
        enum payment_status "PENDING, PAID, FAILED"
        enum delivery_status "PENDING, DELIVERED, CANCELLED"
        datetime transaction_date
        datetime payment_date
        text notes
        datetime created_at
        datetime updated_at
    }
    
    %% RATING & TRUST SYSTEM
    RATING {
        bigint id PK
        bigint rater_id FK "User giving the rating"
        bigint rated_user_id FK "User being rated"
        bigint transaction_id FK
        integer rating_score "1-5"
        enum rating_type "QUALITY, RELIABILITY, PAYMENT, COMMUNICATION"
        text comment
        datetime created_at
        datetime updated_at
    }
    
    %% RELATIONSHIPS
    
    %% Location Hierarchy (One-to-Many)
    PROVINCE ||--o{ DISTRICT : "has many"
    DISTRICT ||--o{ SECTOR : "has many"
    SECTOR ||--o{ CELL : "has many"
    CELL ||--o{ VILLAGE : "has many"
    
    %% User and Location (Many-to-One)
    VILLAGE ||--o{ USER : "has residents"
    
    %% User and UserProfile (One-to-One)
    USER ||--|| USER_PROFILE : "has profile"
    
    %% Warehouse and Location (Many-to-One)
    VILLAGE ||--o{ STORAGE_WAREHOUSE : "has warehouses"
    
    %% User and Warehouse Access (Many-to-Many through junction table)
    USER ||--o{ WAREHOUSE_ACCESS : "has access to"
    STORAGE_WAREHOUSE ||--o{ WAREHOUSE_ACCESS : "grants access to"
    
    %% Inventory Relationships (Many-to-One)
    USER ||--o{ INVENTORY : "farmer stores crops"
    STORAGE_WAREHOUSE ||--o{ INVENTORY : "contains inventory"
    CROP_TYPE ||--o{ INVENTORY : "type of crop"
    USER ||--o{ INVENTORY : "storekeeper logs"
    
    %% Transaction Relationships (Many-to-One)
    INVENTORY ||--o{ TRANSACTION : "sold through"
    USER ||--o{ TRANSACTION : "buyer purchases"
    USER ||--o{ TRANSACTION : "seller/farmer sells"
    
    %% Rating Relationships (Many-to-One)
    USER ||--o{ RATING : "rater gives"
    USER ||--o{ RATING : "rated user receives"
    TRANSACTION ||--o{ RATING : "rated for"
```

## Entity Summary:

### Total Entities: 12

1. **Province** - Top level of Rwandan location
2. **District** - Second level
3. **Sector** - Third level
4. **Cell** - Fourth level
5. **Village** - Fifth level (lowest)
6. **User** - Farmers, Buyers, Storekeepers, Admins
7. **UserProfile** - Extended user information (ONE-TO-ONE)
8. **StorageWarehouse** - Physical storage locations
9. **WarehouseAccess** - Junction table for User-Warehouse (MANY-TO-MANY)
10. **CropType** - Types of crops (Beans, Maize, etc.)
11. **Inventory** - Stored crop records
12. **Transaction** - Purchase/Sale records
13. **Rating** - Trust and rating system

### Relationship Types Covered:

✅ **One-to-One**: User ↔ UserProfile  
✅ **One-to-Many / Many-to-One**: 
  - Province → District → Sector → Cell → Village
  - Village → User
  - Village → StorageWarehouse
  - User (Farmer) → Inventory
  - StorageWarehouse → Inventory
  - CropType → Inventory
  - Inventory → Transaction
  - User (Buyer) → Transaction
  - User (Rater) → Rating

✅ **Many-to-Many**: User ↔ StorageWarehouse (through WarehouseAccess)

### Special Requirements Met:

✅ At least 5 entities (We have 12+)  
✅ Complete Rwandan location hierarchy  
✅ User-Location relationship  
✅ All three relationship types  
✅ Business logic support for the Rangira Agro Farming platform

