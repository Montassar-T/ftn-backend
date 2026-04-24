# Car Services Backend

Spring Boot REST API for managing and tracking vehicle repair services.

## Overview

This is a robust backend service built with Spring Boot that powers the mechanic repair tracking system. It provides comprehensive REST APIs for managing repair jobs, service requests, customer information, and repair tracking operations.

## Tech Stack

- **Framework:** Spring Boot 3.3.5
- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security + JWT Authentication
- **Database Migrations:** Liquibase
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Object Mapping:** ModelMapper 3.2.0
- **JWT:** JJWT 0.11.5
- **Code Quality:** Spotless (Palantir Java Format)
- **Testing:** JUnit 5, Mockito, Spring Security Test
- **Docker:** Containerization ready

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 12+ (or Docker)
- Git

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/Montassar-T/car-services-backend.git
cd car-services-backend
```

#### 2. Set Up Database

**Option A: Using Docker (Recommended)**

```bash
docker-compose up -d
```

This starts a PostgreSQL container with the database pre-configured.

**Option B: Local PostgreSQL Installation**

```bash
createdb car_services_db
```

Then update `application.properties` with your database credentials.

#### 3. Install Dependencies

```bash
./mvnw clean install
```

#### 4. Configure Application Properties

Create or update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/car_services_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL10Dialect

# JWT Configuration
app.jwt.secret=your-secret-key-here-min-256-bits
app.jwt.expiration=86400000
```

#### 5. Run the Application

```bash
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`

## Available Maven Commands

| Command | Description |
|---------|-------------|
| `./mvnw clean install` | Clean and build the project |
| `./mvnw spring-boot:run` | Run the application |
| `./mvnw test` | Run unit tests (excludes integration tests) |
| `./mvnw verify` | Run all tests including integration tests |
| `./mvnw spotless:apply` | Auto-format code with Spotless |
| `./mvnw spotless:check` | Check code formatting |
| `./mvnw clean package` | Build production-ready JAR |

## Project Structure

```
src/
├── main/
│   ├── java/com/carServices/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/           # Business logic
│   │   ├── repository/        # JPA repositories
│   │   ├── entity/            # JPA entities
│   │   ├── dto/               # Data transfer objects
│   │   ├── config/            # Spring configurations
│   │   ├── security/          # Security configurations & JWT
│   │   ├── exception/         # Custom exceptions
│   │   ├── util/              # Utility classes
│   │   └── Application.java   # Main application entry point
│   └── resources/
│       ├── application.properties    # Configuration
│       ├── application-dev.properties
│       └── db/changelog/             # Liquibase migrations
└── test/
    └── java/com/carServices/        # Unit and integration tests
```

## API Documentation

### Swagger UI

Once the application is running, access the interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

```
http://localhost:8080/v3/api-docs
```

## Database Migrations

Database schema changes are managed with Liquibase. Migration files are located in:

```
src/main/resources/db/changelog/
```

Migrations run automatically on application startup.

## Authentication

The API uses JWT (JSON Web Tokens) for authentication:

1. **Register:** `POST /api/auth/register`
2. **Login:** `POST /api/auth/login` - Returns JWT token
3. **Access Protected Endpoints:** Include token in Authorization header:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

## Code Quality

### Spotless Code Formatting

The project uses Spotless with Palantir Java Format for consistent code style:

```bash
# Auto-format code
./mvnw spotless:apply

# Check formatting
./mvnw spotless:check
```

### Testing

#### Unit Tests

```bash
./mvnw test
```

Excludes integration tests marked with `*IntegrationTest.java` or `*IT.java`

#### Integration Tests

```bash
./mvnw verify
```

Includes all tests (unit + integration)

## Docker Deployment

### Build Docker Image

```bash
docker build -t car-services-backend:latest .
```

### Run with Docker Compose

```bash
docker-compose up
```

The `docker-compose.yml` includes:
- Spring Boot application container
- PostgreSQL database container
- Network configuration for inter-container communication

## Configuration Profiles

The application supports multiple profiles:

- **dev:** Development environment
- **prod:** Production environment (default)

Activate a profile in `application.properties`:

```properties
spring.profiles.active=dev
```

## Key Features

- **RESTful API:** Clean, well-documented endpoints
- **JWT Authentication:** Secure token-based authentication
- **Role-Based Access Control:** Fine-grained permission management
- **Data Validation:** Input validation on all API endpoints
- **Error Handling:** Comprehensive global exception handling
- **Database Migrations:** Version-controlled schema changes
- **API Documentation:** Auto-generated Swagger UI
- **Code Quality:** Consistent formatting with Spotless
- **Testing:** Unit and integration test coverage
- **Docker Support:** Easy containerization and deployment

## Dependencies

### Core Dependencies

- `spring-boot-starter-web` - RESTful web services
- `spring-boot-starter-data-jpa` - JPA data access
- `spring-boot-starter-security` - Security & authentication
- `spring-boot-starter-test` - Testing framework

### Database & Migrations

- `postgresql` - PostgreSQL JDBC driver
- `liquibase-core` - Database schema versioning

### Security & JWT

- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` - JWT token handling

### Development Tools

- `lombok` - Reduce boilerplate code
- `modelmapper` - Object mapping
- `springdoc-openapi-starter-webmvc-ui` - Swagger UI

## Troubleshooting

### Database Connection Issues

**Error:** `Connection refused`

**Solution:** Ensure PostgreSQL is running and accessible at the configured URL

```bash
# Using Docker Compose
docker-compose up -d

# Or check PostgreSQL status
psql -U postgres -d postgres -c "SELECT 1"
```

### Build Failures

**Error:** `Maven compilation errors`

**Solution:** Clean and rebuild

```bash
./mvnw clean install -U
```

### JWT Token Issues

**Error:** `Invalid token` or `Token expired`

**Solution:** Verify JWT secret and expiration in `application.properties`

## Development Notes

- This is a private freelance project
- All code follows Java best practices and Spring Boot conventions
- Comprehensive error handling and validation is implemented
- The API is fully documented via Swagger/OpenAPI
- Both unit and integration tests are included

## Support

For issues, feature requests, or clarifications, contact the development team.

---

**Last Updated:** April 24, 2026  
**Java Version:** 17  
**Spring Boot Version:** 3.3.5
