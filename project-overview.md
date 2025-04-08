## 1. Introduction

This document outlines the plan and specifications for building **PortfolioForge**, a web platform enabling users to create, manage, and showcase their own personal portfolio websites. The application will consist of a Vue.js frontend and a Spring Boot backend, developed in separate repositories. Users can register, customize their portfolio content (about me, projects, skills), and share a public link to their generated portfolio.

## 2. Goals

*   Provide a platform for users (developers, designers, creatives) to easily create and host their personal portfolios.
*   Offer customization options for portfolio content (About, Projects, Skills, Contact Info).
*   Ensure secure user registration and authentication.
*   Allow users to manage their own portfolio content through a dedicated interface.
*   Generate publicly accessible portfolio pages for each registered user.
*   Demonstrate proficiency in Vue.js, Spring Boot, REST API design, database management, and user authentication (Spring Security).

## 3. Core Features

### User Authentication & Management:

*   **User Registration:** New users can sign up with email/username and password.
*   **User Login/Logout:** Secure authentication for registered users.
*   **(Optional) Password Reset:** Functionality to reset forgotten passwords.

### User Portfolio Management (Authenticated Users):

*   **Portfolio Dashboard:** Central area for managing portfolio content.
*   **Portfolio Settings:** Update About Me text, contact details (Email, LinkedIn, GitHub), resume link, potentially profile picture URL, choose a unique public slug/username.
*   **Project Management:** CRUD operations for personal projects (Title, Description, Technologies, Image URL, Demo/Repo Links).
*   **Skill Management:** CRUD operations for personal skills (Name, Category, Icon).
*   **(Optional) Contact Message Viewing:** View messages submitted through their public contact form.

### Public Portfolio Viewing (Unauthenticated Visitors):

*   **Unique Portfolio URL:** Access a user's portfolio via a unique identifier (e.g., `portfolioforge.com/username` or `username.portfolioforge.com` - requires more setup).
*   **Display Portfolio Content:** View the specific user's:
    *   Homepage/About section.
    *   Skills section.
    *   Projects page/section.
    *   Contact form (submits message associated with *that* portfolio owner).

## 4. Technology Stack

*   **Frontend:**
    *   Framework: Vue.js 3
    *   Build Tool: Vite
    *   Routing: Vue Router
    *   State Management: Pinia (managing auth state is critical)
    *   HTTP Client: Axios (interceptors for auth tokens)
    *   Styling: Tailwind CSS (or chosen alternative)
*   **Backend:**
    *   Framework: Spring Boot 3+
    *   Language: Java 17+
    *   Build Tool: Maven (or Gradle)
    *   API: Spring Web (REST Controllers)
    *   Data Persistence: Spring Data JPA (Hibernate)
    *   **Authentication/Authorization:** Spring Security (likely JWT or Session-based)
    *   Database: PostgreSQL (Production), H2/MySQL (Development/Testing)
    *   Validation: Spring Validation
    *   (Optional): Lombok, MapStruct
*   **Database:** MySQL (as currently configured) / PostgreSQL (recommended for production features)
*   **API:** RESTful API using JSON

## 5. Architecture

*   **Decoupled Frontend/Backend:** Two separate applications.
*   **Communication:** Frontend communicates with Backend via stateless RESTful API (likely using JWT Bearer tokens for authenticated requests).
*   **Multi-Tenant Data:** Each user's data (Portfolio, Projects, Skills, Messages) is logically separated and associated with their User account. Authorization rules prevent users from accessing/modifying other users' data.
*   **Security:** Spring Security manages authentication and endpoint authorization (distinguishing public vs. user-specific vs. admin endpoints).

## 6. Data Models (Backend - JPA Entities)

*   **`User`:** `id` (Long), `username` (String, unique), `email` (String, unique), `password` (String, hashed), `roles` (e.g., Set<Role> or String), `createdAt`, `updatedAt`.
*   **`Portfolio`:** `id` (Long), `aboutMeText` (TEXT), `resumeUrl` (String), `linkedInUrl` (String), `githubUrl` (String), `contactEmail` (String), `publicSlug` (String, unique, optional), `lastUpdatedAt`.
    *   **Relationship:** `@OneToOne` relationship with `User` (a User has one Portfolio). `private User user;`
*   **`Project`:** `id`, `title`, `description`, `technologies`, `imageUrl`, `liveUrl`, `repoUrl`, `displayOrder`, `createdAt`, `updatedAt`.
    *   **Relationship:** `@ManyToOne` relationship with `User` (a User can have many Projects). `private User user;`
*   **`Skill`:** `id`, `name`, `category`, `icon`.
    *   **Relationship:** `@ManyToOne` relationship with `User` (a User can list many Skills). `private User user;`
*   **`ContactMessage`:** `id`, `senderName`, `senderEmail`, `subject`, `message`, `submissionDate`, `isRead`.
    *   **Relationship:** `@ManyToOne` relationship with `User` (a message is sent *to* a specific User/Portfolio owner). `private User recipient;`
*   **(Optional) `Role`:** `id` (Integer), `name` (Enum or String, e.g., 'ROLE_USER', 'ROLE_ADMIN'). Used with Spring Security.

## 7. API Endpoints (Examples - High Level)

*   **Authentication:**
    *   `POST /api/v1/auth/register`: Register a new user.
    *   `POST /api/v1/auth/login`: Authenticate a user, return token/session info.
    *   `POST /api/v1/auth/logout`: (If session-based or using token blocklist).
*   **Public Portfolio Data:**
    *   `GET /api/v1/portfolios/{usernameOrSlug}`: Get public portfolio details for a user.
    *   `GET /api/v1/portfolios/{usernameOrSlug}/projects`: Get public projects for a user.
    *   `GET /api/v1/portfolios/{usernameOrSlug}/skills`: Get public skills for a user.
    *   `POST /api/v1/portfolios/{usernameOrSlug}/contact`: Submit contact message to a specific user.
*   **Authenticated User ("Me") Endpoints:** (Require Auth Token/Session)
    *   `GET /api/v1/me`: Get details of the currently logged-in user.
    *   `GET /api/v1/me/portfolio`: Get the current user's portfolio data.
    *   `PUT /api/v1/me/portfolio`: Update the current user's portfolio data.
    *   `GET /api/v1/me/projects`: Get the current user's projects.
    *   `POST /api/v1/me/projects`: Create a new project for the current user.
    *   `GET /api/v1/me/projects/{projectId}`: Get a specific project of the current user.
    *   `PUT /api/v1/me/projects/{projectId}`: Update a specific project of the current user.
    *   `DELETE /api/v1/me/projects/{projectId}`: Delete a specific project of the current user.
    *   (Similar CRUD endpoints for `/api/v1/me/skills`)
    *   `GET /api/v1/me/contact-messages`: Get contact messages received by the current user.
    *   `PATCH /api/v1/me/contact-messages/{messageId}`: Mark a message as read/unread.

## 8. Project Setup

*   **Backend Repository:** `portfolioforge-backend`
    *   Dependencies: Web, JPA, DB Driver, Validation, **Spring Security**, Lombok (optional).
*   **Frontend Repository:** `portfolioforge-frontend`
    *   Dependencies: Vue Router, Pinia, Axios, Styling lib. Need auth handling logic.

## 9. Development Phases (Revised High-Level)

1.  **Setup & Core User:** Initialize projects, add Spring Security, Implement `User` entity, repository, service (with password hashing), basic `SecurityConfig`.
2.  **Authentication API:** Implement registration and login controllers/endpoints. Choose JWT or session strategy.
3.  **Portfolio Entity & API:** Implement `Portfolio` entity (linked to User), repository, service, DTO, and secured `/api/v1/me/portfolio` CRUD endpoints.
4.  **Project Entity & API:** Add `user_id` to `Project` entity, update repository, service, DTO, and implement secured `/api/v1/me/projects` CRUD endpoints.
5.  **Skill Entity & API:** Implement `Skill` entity (linked to User), repository, service, DTO, and implement secured `/api/v1/me/skills` CRUD endpoints.
6.  **Public API Endpoints:** Implement the public `/api/v1/portfolios/{usernameOrSlug}/...` endpoints (read-only).
7.  **Contact Message:** Implement `ContactMessage` entity (linked to recipient User), repository, service, DTO, and endpoints (`POST` public, `GET`/`PATCH` private).
8.  **Frontend Auth:** Implement login/registration forms, routing guards, auth state management (Pinia), Axios interceptors for tokens.
9.  **Frontend User Dashboard:** Create views/components for users to manage their portfolio, projects, skills.
10. **Frontend Public View:** Create components to display a specific user's portfolio based on URL parameter.
11. **Testing:** Unit, integration (especially security), and E2E tests.
12. **Refinement & Deployment Prep:** Final checks, documentation, environment variables.
13. **Deployment:** Deploy backend and frontend. Configure domains/subdomains if needed for public URLs.