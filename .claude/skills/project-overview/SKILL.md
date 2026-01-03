
---
name: project-overview
description: Axis Platform microservices architecture overview. Use when asking about project structure, service responsibilities, inter-service communication, database usage, or when needing context about the overall system before implementing features.
---

# Axis Platform Architecture

## System Overview

Axis Platform is a microservices-based life goals planning system (similar to Trello) for organizing and tracking long-term, medium-term, and short-term life goals with enterprise-grade authentication and authorization.

**Tech Stack:**
- Backend: Spring Boot 3.4.1, Spring Cloud 2024.0.0, Java 21
- Authentication: Keycloak 24.0 (OAuth2/OIDC)
- Databases: PostgreSQL (structured data), MongoDB (content/documents)
- Message Queue: RabbitMQ 3
- Cache: Redis 7
- Infrastructure: Kubernetes (Minikube for dev), Skaffold
- Communication: REST APIs, async messaging (RabbitMQ)

## Microservices

**Currently Implemented:**

| Service         | Port | Database      | Responsibility                           |
|-----------------|------|---------------|------------------------------------------|
| axis-gateway    | 8080 | -             | API Gateway (Spring Cloud Gateway), JWT auth, routing |
| axis-media      | 8083 | MongoDB       | Media file management                    |
| axis-common     | -    | -             | Shared library: security, exceptions, DTOs |

**Planned for Future:**

| Service         | Port | Database      | Responsibility                           |
|-----------------|------|---------------|------------------------------------------|
| axis-goal       | 8081 | PostgreSQL    | Goals management (long, medium, short-term goals) |

## Service Communication

```
[Client] → [Gateway:8080] → [Goal/Media]
                ↓
    [Keycloak JWT Validation]
         [Path-based routing]
```

**Patterns:**
- Sync: REST calls between services
- Auth: JWT tokens issued by Keycloak, validated at gateway and each service
- Discovery: Kubernetes DNS (e.g., `http://axis-media:8083`)
- Gateway routing based on path patterns

**Authentication & Authorization:**
- Keycloak realm: `axis`
- Client: `axis-backend`
- Test user: `testuser/testuser`
- Custom `JwtAuthenticationConverter` extracts user information from JWT tokens
- `SecurityUtils` helper provides access to user ID, email, username from JWT

## Database Strategy

**PostgreSQL** (structured, relational data):
- Goals data (long-term, medium-term, short-term goals) (axis-goal - planned)
- Audit logs
- Configuration
- **Note:** All entities use UUID primary keys
- **Migrations:** Flyway in `src/main/resources/db/migration/`
- **JPA mode:** `ddl-auto: validate` (never auto-generate schema)

**MongoDB** (flexible, document data):
- Media files and metadata (axis-media)

## Project Structure

```
axis-backend/
├── axis-gateway/          # API Gateway (WebFlux)
├── axis-media/            # Media service (Web MVC)
├── axis-common/           # Shared library
├── k8s/                   # Kubernetes manifests
│   ├── namespace.yaml
│   ├── config/           # ConfigMaps, Secrets
│   ├── infrastructure/   # Postgres, Keycloak, MongoDB, RabbitMQ, Redis
│   └── services/         # Service deployments
├── skaffold.yaml         # Skaffold configuration
└── build.gradle          # Root Gradle build
```

## Key Architecture Conventions

**Clean Architecture Layers:**
```
Entity → Repository → Service (Interface + Impl) → Controller → DTOs
```

**Mandatory patterns:**
- UUID primary keys for all entities (`@GeneratedValue(strategy = GenerationType.UUID)`)
- Service layer: Interface first, then implementation
- DTOs for API contracts, never expose entities
- MapStruct for entity-DTO conversions
- Constructor injection (no field `@Autowired`)
- OpenAPI documentation on all endpoints
- Bean Validation on DTOs

**Shared components (axis-common):**
- `GlobalExceptionHandler`: Centralized exception handling
- `SecurityUtils`: Extract user info from JWT
- `JwtAuthenticationConverter`: Extract user information from JWT tokens
- `ApiError`: Standardized error response DTO

## Development Environment

**Local deployment:**
```bash
minikube start
eval $(minikube docker-env)
skaffold dev  # Auto-reload on code changes
```

**Access points:**
- Gateway: http://localhost:8080
- Keycloak Admin: http://localhost:8180
- RabbitMQ Management: http://localhost:15672

**Build:**
```bash
./gradlew clean build                 # All services
./gradlew :axis-media:build           # Single service
```

For detailed information, see `CLAUDE.md` in the repository root.
