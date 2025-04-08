# PortfolioForge Backend - Development Guidelines

This document outlines technical details, architectural choices, and setup procedures specific to developing the PortfolioForge backend application.

## 1. Technology Stack

*   **Framework:** Spring Boot (Version: X.Y.Z - *Update with your version*)
*   **Language:** Java (Version: 17+)
*   **Build Tool:** Maven
*   **Database:** MySQL
*   **Persistence:** Spring Data JPA (Hibernate)
*   **API:** Spring Web (REST)
*   *(Add other key libraries like Lombok, Validation, etc.)*

## 2. Architecture

The backend follows a layered architecture pattern, influenced by Domain-Driven Design (DDD) principles:

*   **Controllers (`controller` package):** Handle incoming HTTP requests, validate input (often using DTOs), delegate business logic to Services, and format HTTP responses. They should remain thin and primarily handle web concerns.
*   **Services (`service` package):** Contain the core business logic. They orchestrate calls to Repositories and other services. Transactions are typically managed at this layer.
*   **Repositories (`repository` package):** Interfaces extending Spring Data JPA's `JpaRepository` (or similar). Responsible for data access and persistence operations. Define custom queries here if needed.
*   **Entities (`entity` package):** JPA-annotated Plain Old Java Objects (POJOs) representing the core domain data and mapped to database tables.
*   **DTOs (`dto` package):** Data Transfer Objects used for transferring data between layers, especially between Controller and Service, and as request/response bodies in the API. This helps decouple the API contract from the internal domain model (Entities).
*   **Exceptions (`exception` package):** Custom exception classes and global exception handling (`@ControllerAdvice`) for consistent error responses.
*   **Configuration (`config` package):** Spring configuration classes (e.g., SecurityConfig, Bean definitions).

*(**Note on DDD:** If you are consciously applying specific DDD patterns like Aggregates, Value Objects, Bounded Contexts (though less likely in a single portfolio app), explicitly mention them here.)*

## 3. Database Setup (Local Development)

The application is configured to simplify local database setup using MySQL.

*   **Database Name:** `portfolioforge_db` (as defined in `application.properties`)
*   **Automatic Database Creation:** The JDBC URL in `application.properties` includes `createDatabaseIfNotExist=true`. This allows Spring Boot to attempt creating the database on startup *if the configured MySQL user has the necessary privileges*. **This is a development convenience and should not be relied upon for production.** Ensure your local MySQL user (`spring.datasource.username`) has `CREATE DATABASE` privileges if you want to use this feature. The recommended standard approach is still to create the database manually first:
    ```sql
    CREATE DATABASE portfolioforge_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
*   **Automatic Schema Management (DDL):** The property `spring.jpa.hibernate.ddl-auto` in `application.properties` is set to `update`. This means Hibernate will automatically attempt to update the database schema (add tables, columns, etc.) to match the defined `@Entity` classes upon application startup.
    *   **Caution:** While useful for development, `update` can have limitations and risks.
    *   **Production:** For production environments, `ddl-auto` should be set to `validate` (to verify schema matches entities) or `none`. Database schema migrations should be handled externally using tools like Flyway or Liquibase. **Never use `create` or `create-drop` in production as it will delete data.**

## 4. Configuration Management

*   Primary configuration is done in `src/main/resources/application.properties`.
*   Sensitive information like database passwords should ideally be externalized using environment variables or Spring profiles for non-local environments. Example for overriding DB password with an environment variable: `SPRING_DATASOURCE_PASSWORD=your_secure_pass ./mvnw spring-boot:run`

## 5. Running the Application Locally

1.  Ensure MySQL server is running.
2.  Verify database `portfolioforge_db` exists (or rely on `createDatabaseIfNotExist=true` with appropriate user privileges).
3.  Navigate to the project root directory in your terminal.
4.  Run the application using the Maven wrapper:
    *   Linux/macOS: `./mvnw spring-boot:run`
    *   Windows: `.\mvnw.cmd spring-boot:run`
5.  The application will be available at `http://localhost:8080` (or the configured `server.port`).

## 6. Coding Conventions

*   *(Optional: Add any specific conventions you want to follow, e.g., naming, formatting, use of Lombok, final keywords, etc.)*

---