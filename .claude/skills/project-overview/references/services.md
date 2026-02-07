# Service Details

## Currently Implemented Services

### axis-gateway (Port 8080)

API Gateway - single entry point for all clients.

**Responsibilities:**
- Route requests to appropriate services
- JWT authentication/authorization with Keycloak
- CORS handling
- Request/response logging

**Technology:**
- Spring Cloud Gateway (WebFlux/Reactive)
- Spring Security OAuth2 Resource Server
- Keycloak JWT validation

**Configuration:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: media-service
          uri: http://axis-media:8083
          predicates:
            - Path=/api/media/**
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://keycloak:8080/realms/axis/protocol/openid-connect/certs
```

**Security:**
- All routes require valid JWT (except `/actuator/**`)
- Custom `JwtAuthenticationConverter` extracts user information from JWT tokens
- Provides user context (user ID, email, username) to downstream services

---

### axis-media (Port 8083)

Media file management service.

**Responsibilities:**
- File upload/download
- Image processing (resize, thumbnails)
- Metadata extraction
- Storage management (MongoDB)

**Technology:**
- Spring Boot Web (MVC)
- Spring Data MongoDB
- Spring Security OAuth2 Resource Server

**Database:** MongoDB (axis_media)
- Collection: `media` - stores file metadata and content

**Key Endpoints:**
```
POST   /api/media/upload
GET    /api/media/{id}
GET    /api/media
DELETE /api/media/{id}
GET    /api/media/health
```

**Architecture:**
- Clean layered architecture: Document → Repository → Service (Interface + Impl) → Controller → DTOs
- MongoDB ObjectId for document IDs
- Global exception handling via axis-common

---

### axis-common (Shared Library)

Common components used across all microservices.

**Components:**
- **Security:**
  - `JwtAuthenticationConverter`: Extracts user information from Keycloak JWT tokens
  - `SecurityUtils`: Helper methods to extract user ID, email, username from JWT

- **Exception Handling:**
  - `GlobalExceptionHandler`: Centralized exception handling with `@RestControllerAdvice`
  - `ResourceNotFoundException`: 404 exception
  - `BusinessException`: Configurable HTTP status exception
  - `ApiError`: Standardized error response DTO

- **DTOs:**
  - `ApiError`: Error response with timestamp, status, message, path, field errors

**Usage:**
All microservices depend on axis-common and inherit these capabilities automatically.

---

## Planned Future Services

### axis-goal (Port 8081)

Goals management service.

**Planned Responsibilities:**
- CRUD for goals (long-term, medium-term, short-term)
- Goal status tracking
- Progress monitoring
- Goal categories and tags

**Planned Database:** PostgreSQL (axis_goal)
- Table: `users` - cached user profiles from Keycloak
- Table: `goals` - goals with type, status, dates

**Planned Technology:**
- Spring Boot Web (MVC)
- Spring Data JPA + PostgreSQL
- Liquibase migrations
- Spring Security OAuth2 Resource Server

---

### axis-board (Port 8084)

Board management service (Trello-like).

**Planned Responsibilities:**
- Create and manage boards
- Column/list management
- Card positioning and ordering
- Drag-and-drop support

**Planned Database:** PostgreSQL
- Table: `boards`
- Table: `columns`
- Table: `cards` (links to goals)

---

## Service Communication

All services communicate through:
- **Synchronous:** REST APIs via gateway
- **Asynchronous:** RabbitMQ for event-driven communication (future)
- **Service Discovery:** Kubernetes DNS (e.g., `http://axis-media:8083`)

**Authentication Flow:**
1. Client obtains JWT from Keycloak
2. Client sends request to Gateway with JWT in Authorization header
3. Gateway validates JWT and routes to appropriate service
4. Service validates JWT independently and extracts user context
5. Service processes request with user context from JWT