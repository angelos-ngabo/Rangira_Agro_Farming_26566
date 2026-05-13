<div align="center">
  <h1>🌾 Rangira Agro-Farming Management System</h1>
  <p><i>A modern, role-based platform optimizing agricultural supply chains, digital warehousing, and marketplace operations.</i></p>

  <p>
    <img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java 17" />
    <img src="https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg" alt="Spring Boot" />
    <img src="https://img.shields.io/badge/React-18-blue.svg" alt="React" />
    <img src="https://img.shields.io/badge/PostgreSQL-15-blue.svg" alt="PostgreSQL" />
    <img src="https://img.shields.io/badge/Docker-Enabled-blue.svg" alt="Docker" />
    <img src="https://img.shields.io/badge/Tests-17_Passing-success.svg" alt="JUnit Tests" />
  </p>
</div>

---

## 📖 Project Overview

**Rangira Agro-Farming** is a comprehensive, full-stack digital solution designed to bridge the gap between farmers, buyers, and warehouse administrators. It digitizes the agricultural supply chain by offering secure role-based access, automated inventory and capacity management, integrated wallet systems, and real-time marketplace functionalities.

This system was engineered with **Software Engineering best practices** to deliver a scalable, maintainable, and production-ready application suitable for high-volume agricultural operations.

---

## ✨ Main Features

- 🔐 **Secure Authentication:** JWT-based stateless authentication with OTP email verification and secure password resets.
- 👥 **Role-Based Access Control (RBAC):** Tailored dashboards and permissions for Farmers, Buyers, Storekeepers, and Admins.
- 📦 **Inventory & Warehouse Management:** Automated capacity checking, crop grading, and real-time stock tracking.
- 🛒 **Marketplace:** Public-facing crop listings, dynamic search filters, and direct buyer-farmer enquiries.
- 💳 **Digital Wallet System:** Internal transactional ledgers, balance tracking, and OTP-secured withdrawal requests.
- 💬 **Integrated Messaging:** Internal communication system and real-time system notifications.
- ⭐ **Rating System:** Trust-building ratings for farmers based on crop quality and past transactions.
- 🐳 **Dockerized Deployment:** Fully containerized backend, frontend, and database utilizing multi-stage builds and Nginx.
- 🧪 **Comprehensive Automated Testing:** High-coverage JUnit 5 and Mockito suite isolating service and controller layers.
- 🖥️ **System Diagnostics Dashboard:** Custom browser-based health checking, connectivity monitoring, and token validation.

---

## 🎭 User Roles

| Role | Description & Capabilities |
| :--- | :--- |
| **FARMER** | Can store crops in warehouses, manage their digital wallet, withdraw funds, respond to buyer enquiries, and track inventory statuses (Stored, Partially Sold, Sold, Withdrawn). |
| **BUYER** | Can browse the public marketplace, send enquiries for specific crops, initiate transactions, and leave ratings for farmers. |
| **STOREKEEPER** | Manages physical warehouse operations, approves/rejects incoming inventory requests, validates crop quality, and updates warehouse storage capacities. |
| **ADMIN** | Has ultimate oversight. Manages users, configures warehouse definitions, oversees system health, and handles all transactional approvals. |

---

## 🛠️ Tech Stack

### Backend
| Technology | Description |
| :--- | :--- |
| **Spring Boot 3.x** | Core backend framework |
| **Java 17** | Programming language |
| **Spring Security & JWT** | Stateless authentication and authorization |
| **Spring Data JPA & Hibernate**| ORM and database interactions |

### Frontend
| Technology | Description |
| :--- | :--- |
| **React 18** | UI Library |
| **Axios** | API requests with JWT interceptors |
| **React Router DOM** | Client-side routing |
| **Recharts / Chart.js** | Data visualization and analytics |

### Infrastructure & Tooling
| Technology | Description |
| :--- | :--- |
| **PostgreSQL** | Primary relational database |
| **Docker & Docker Compose** | Containerization and orchestration |
| **Nginx** | Reverse proxy and frontend static file serving |
| **JUnit 5 & Mockito** | Automated Unit and Integration Testing |
| **Maven** | Build automation and dependency management |

---

## 🏗️ System Architecture

The application strictly follows a **Layered Architecture** leveraging the MVC pattern to enforce Separation of Concerns:

1. **Presentation Layer (Frontend):** React SPA communicating via RESTful JSON APIs.
2. **API/Controller Layer:** Spring `@RestController` mapping endpoints and validating incoming DTOs.
3. **Service Layer:** Contains the core business logic, transactional boundaries (`@Transactional`), and capacity management logic.
4. **Data Access Layer:** Spring Data JPA `@Repository` interfaces for robust PostgreSQL interactions.
5. **Security Layer:** Custom JWT filters intercepting all requests to validate claims and inject the `SecurityContext`.

### Docker Architecture
The system utilizes a custom Docker network containing three primary containers:
- `postgres-db`: Persistent database with mounted volume bindings.
- `backend-api`: Spring Boot JAR running on OpenJDK, connected securely to the database.
- `frontend-web`: Multi-stage build compiling React and serving the optimized production build via Nginx.

---

## 📂 Project Structure

```text
Rangira_Agro_Farming/
├── src/main/java/com/raf/
│   ├── config/        # Security, CORS, and Seeder configurations
│   ├── controller/    # REST API endpoints
│   ├── dto/           # Data Transfer Objects & Validation definitions
│   ├── entity/        # JPA Database Models
│   ├── enums/         # Status and Type enumerations
│   ├── exception/     # Global Exception Handling
│   ├── repository/    # Spring Data JPA Interfaces
│   ├── security/      # JWT Utilities and Filters
│   └── service/       # Core Business Logic Services
├── src/test/java/...  # JUnit 5 & Mockito Automated Tests
├── frontend/          # React SPA
│   ├── src/pages/     # Core UI Views (Marketplace, Dashboards)
│   ├── src/services/  # Axios Interceptors and API integrations
│   └── Dockerfile     # Nginx-based multi-stage frontend build
├── Dockerfile         # Backend Maven build configuration
├── docker-compose.yml # Multi-container orchestration
└── pom.xml            # Maven Dependencies
```

---

## 🚀 Installation and Setup

### Prerequisites
- Docker & Docker Compose
- Java 17 (for local non-Docker development)
- Node.js & npm (for local non-Docker UI development)
- Maven

### 🐳 Running via Docker (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/angelos-ngabo/Rangira_Agro_Farming_26566.git
   cd Rangira_Agro_Farming
   ```

2. **Start the environment:**
   ```bash
   docker compose up --build
   ```

3. **Access the application:**
   - Frontend: `http://localhost:3000`
   - Backend APIs: `http://localhost:8080/api/...`

4. **Stop the environment:**
   ```bash
   docker compose down
   ```

### 💻 Running Locally (Without Docker)

1. **Database:** Ensure PostgreSQL is running locally (`localhost:5432`) with a database named `rangira_agro_db`.
2. **Backend:**
   ```bash
   mvn clean spring-boot:run
   ```
3. **Frontend:**
   ```bash
   cd frontend
   npm install
   npm start
   ```

---

## 🔐 Environment Variables

Key configurations are managed via `application.properties` and Docker environment variables:
- `SPRING_DATASOURCE_URL`: PostgreSQL connection string.
- `JWT_SECRET`: Base64 encoded secure secret for token generation.
- `JWT_EXPIRATION`: Token lifespan configurations.
- `MAIL_USERNAME` / `MAIL_PASSWORD`: SMTP credentials for OTP emails.

---

## 🧪 Testing

The system employs a robust, isolated testing strategy. Tests are executed against an **H2 In-Memory Database** using the `application-test.properties` configuration, guaranteeing that production/development databases are never polluted during test execution.

**To run the automated test suite:**
```bash
mvn test
```

### Testing Scope
- **Controllers:** `@WebMvcTest` with `MockMvc` validating API routing, DTO validation (`@Valid`), and Security filters.
- **Services:** `@ExtendWith(MockitoExtension.class)` ensuring pure business logic validation (e.g., wallet insufficient funds checks, inventory capacity reduction).
- **Repositories:** `@DataJpaTest` validating custom JPA queries and entity mappings.

> **Status:** The suite currently contains 17 fully passing, highly optimized tests.

---

## 📊 Browser-Based System Diagnostics

The project features a dedicated **System Testing Page** built into the frontend dashboard (`/system-testing`). 
This UI tool allows developers and evaluators to:
- Monitor backend API health in real-time.
- Verify JWT Authentication token validity directly in the browser.
- Test endpoint connectivity without requiring external tools like Postman.

---

## ⚙️ Software Engineering Best Practices

- **SOLID Principles:** Interfaces, single-responsibility services, and dependency injection heavily utilized.
- **DTO Pattern:** Complete separation between Database Entities and client-facing Payloads to prevent over-posting vulnerabilities.
- **Global Exception Handling:** `@ControllerAdvice` gracefully mapping business exceptions (e.g., `ResourceNotFoundException`) to standard HTTP responses.
- **Data Validation:** Strict Jakarta Bean Validation (`@NotNull`, `@NotBlank`) enforced at the controller level.
- **Clean Code & JavaDoc:** Professional inline documentation explaining core business flows and security contexts.

---

## 🔮 Future Improvements

- **Mobile Application:** Integration via React Native leveraging the existing RESTful APIs.
- **Payment Gateway Integration:** Connecting the internal digital wallet to Mobile Money (MTN MoMo/Airtel Money) or Stripe.
- **IoT Integration:** Real-time warehouse temperature and humidity tracking linked to specific inventory lots.

---

## 👨‍💻 Author

**Uwase Lisa Ornella**  
*Academic Project / Capstone Software Engineering Showcase*
