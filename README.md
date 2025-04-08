# PortfolioForge - Backend API

This repository contains the Spring Boot backend REST API for the PortfolioForge application. PortfolioForge is a platform designed to allow users to create, manage, and showcase their personal portfolio websites.

This backend handles:
*   User registration and authentication (using JWT).
*   CRUD operations for user-specific portfolio content (profile details, projects, skills).
*   Receiving and managing contact messages sent to users.
*   Providing public, read-only endpoints to display portfolio data.

## Features

*   **Authentication:** Secure user registration and login using JWT.
*   **Portfolio Management:** Endpoints for users to create/update their main profile information (about text, contact links, resume URL, public slug).
*   **Project Management:** CRUD endpoints for users to manage their individual projects.
*   **Skill Management:** CRUD endpoints for users to manage their listed skills.
*   **Contact Messages:** Public endpoint to receive messages, private endpoints for users to view their messages.
*   **Public Views:** Read-only endpoints to fetch portfolio data based on a user's public slug.

## Technology Stack

*   **Framework:** Spring Boot 3.x
*   **Language:** Java 17+
*   **Build Tool:** Maven
*   **Database:** MySQL (configured, PostgreSQL recommended for production features)
*   **Data Persistence:** Spring Data JPA (Hibernate)
*   **Authentication/Authorization:** Spring Security 6.x (JWT)
*   **API:** Spring Web (REST Controllers)
*   **Validation:** Jakarta Bean Validation
*   **Libraries:** Lombok, JJWT (for JWT handling)

## Prerequisites

Before you begin, ensure you have the following installed:
*   **JDK 17** or higher
*   **Maven 3.8** or higher
*   **MySQL Server** (Version 8.x recommended)
*   **Git**

## Setup Instructions

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/YourUsername/portfolioforge-backend.git
    cd portfolioforge-backend
    ```
    *(Replace `YourUsername` with your actual GitHub username)*

2.  **Create MySQL Database:**
    Connect to your local MySQL server and create the database.
    ```sql
    CREATE DATABASE portfolioforge_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    -- Optional: Create a dedicated user
    -- CREATE USER 'portfolio_user'@'localhost' IDENTIFIED BY 'your_password';
    -- GRANT ALL PRIVILEGES ON portfolioforge_db.* TO 'portfolio_user'@'localhost';
    -- FLUSH PRIVILEGES;
    ```

3.  **Configure Application Properties:**
    Open the `src/main/resources/application.properties` file.
    *   **Database Connection:** Update the following properties with your MySQL details:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/portfolioforge_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
        spring.datasource.username=your_db_user # Replace with your MySQL username (e.g., root or portfolio_user)
        spring.datasource.password=your_db_password # Replace with your MySQL password
        ```
    *   **JWT Secret:** Update the `portfolioforge.jwt.secret` property.
        ```properties
        # IMPORTANT: Use a strong, Base64-encoded secret for production, managed via environment variables!
        portfolioforge.jwt.secret=YourGeneratedBase64EncodedSecretKeyHere # <<< REPLACE THIS!
        portfolioforge.jwt.expiration-ms=3600000 # (Optional: Adjust token expiration time)
        ```
        **Security Warning:** Never commit real secret keys directly. For production, use environment variables (e.g., `JWT_SECRET_KEY`) and reference it like `${JWT_SECRET_KEY:YourDefaultDevSecretHere}`. Generate a secure Base64 key (at least 512 bits / 64 bytes).

    *   **(Development) Schema Generation:** The property `spring.jpa.hibernate.ddl-auto=update` is set for development. This tells Hibernate to automatically create/update tables based on your entities. For production, change this to `validate` or `none` and use database migration tools like Flyway or Liquibase.

4.  **Build the Project (Optional but Recommended):**
    This downloads dependencies and compiles the code.
    ```bash
    ./mvnw clean install -DskipTests
    ```
    (Use `mvnw.cmd` on Windows)

## Running the Application

1.  **Using Maven Wrapper:**
    ```bash
    ./mvnw spring-boot:run
    ```
    (Use `mvnw.cmd` on Windows)

2.  **Using an IDE:**
    You can run the main application class `PortfolioforgeBackendApplication.java` directly from your IDE (like IntelliJ IDEA or Eclipse/STS).

The application will start, and the API will be available at `http://localhost:8080` (or the port configured in `application.properties`).

## API Endpoints Overview

The API follows RESTful principles and uses JSON. Base path: `/api/v1`

*   **/auth/**: User registration (`/register`) and login (`/login`). (Public)
*   **/portfolios/{slugOrUsername}/**: Public endpoints to view portfolio data, projects, skills, and submit contact messages. (Public)
*   **/me/**: Endpoints for the authenticated user to manage their own data (portfolio, projects, skills, messages). Requires JWT Bearer token in the `Authorization` header. (Protected)

Use tools like Postman or Insomnia to interact with the API.

## Environment Variables (Recommended for Production)

*   `SPRING_DATASOURCE_USERNAME`: Database username.
*   `SPRING_DATASOURCE_PASSWORD`: Database password.
*   `JWT_SECRET_KEY`: Your secure, Base64-encoded JWT secret key.

## Related Repositories

*   **Frontend:** [Link to your portfolioforge-frontend repository once created]

## Contributing

(Add contribution guidelines if applicable)

## License

(Specify your license, e.g., MIT, Apache 2.0, or leave blank if private)

---
*.gitignore Status:* Make sure you have a `.gitignore` file (Spring Initializr usually adds a good one) to prevent committing unnecessary files (like compiled code in `target/`, IDE files, `.env` files). If not, create one.
