# Changelog

All notable changes to the Axis Backend project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned
- axis-goal microservice for goals management
- Board system for organizing goals
- Progress tracking and analytics
- Collaboration features

## [0.2.0] - 2024-12-21

### Added
- **axis-media** microservice for media management with MongoDB storage
- MCP (Model Context Protocol) integration for database access
- Agent-based development tooling (.claude/agents/)
- Skills system for project-specific AI assistance (.claude/skills/)
- Health check endpoints for all services
- Media upload and retrieval functionality

### Changed
- Enhanced development workflow with specialized agents
- Improved project documentation structure
- Optimized Skaffold configuration for faster development cycles

### Fixed
- Skaffold startup configuration issues
- Development environment initialization problems
- MongoDB connection reliability

## [0.1.0] - 2024-12-06

### Added
- **axis-gateway** microservice (Spring Cloud Gateway with WebFlux)
- **axis-common** shared library (security utilities, exception handling, DTOs)
- Keycloak integration for OAuth2/OIDC authentication
- PostgreSQL databases (separate for Keycloak and application data)
- MongoDB integration for document storage
- RabbitMQ message broker for async communication
- Redis caching layer
- Kubernetes deployment configuration (Minikube)
- Skaffold for local development workflow
- Flyway database migration support
- MapStruct for DTO conversions
- Global exception handling with `@RestControllerAdvice`
- JWT authentication with custom converter for Keycloak realm roles
- Security utilities (`SecurityUtils`) for user context access
- Actuator endpoints for health checks and metrics
- OpenAPI/Swagger documentation support

### Infrastructure
- Kubernetes namespace `axis`
- PostgreSQL for Keycloak (port 5434)
- PostgreSQL for application data (port 5433)
- Keycloak realm `axis` with client `axis-backend`
- Test user: `testuser`/`testuser`
- Gateway routing configuration for microservices
- Docker image builds via Jib plugin
- ConfigMaps and Secrets for environment configuration

### Architecture
- Clean architecture with layered approach (Entity → Repository → Service → Controller → DTO)
- UUID primary keys for all entities
- Service interface pattern with implementation classes
- DTO pattern for API request/response
- Centralized security configuration in axis-common
- Reactive gateway with WebFlux
- Standard Spring MVC for microservices

---

## How to Update This Changelog

When making changes to the project, add entries under `[Unreleased]` section:

### Categories

- **Added** - New features, services, endpoints, or capabilities
- **Changed** - Changes to existing functionality, refactoring, architectural updates
- **Deprecated** - Features that will be removed in future versions
- **Removed** - Features or services that have been removed
- **Fixed** - Bug fixes
- **Security** - Security patches or improvements

### Example Entry

```markdown
## [Unreleased]

### Added
- New goal management endpoints in axis-goal service
- Support for long-term, medium-term, and short-term goals

### Fixed
- JWT token expiration handling in gateway
```

### Creating a Release

When ready to release a new version:

1. Move all `[Unreleased]` entries to a new version section
2. Add the version number and date: `## [X.Y.Z] - YYYY-MM-DD`
3. Create a git tag: `git tag -a vX.Y.Z -m "Release version X.Y.Z"`
4. Update the `[Unreleased]` section header

### Version Numbers (Semantic Versioning)

- **MAJOR** (X.0.0) - Breaking changes, incompatible API changes
- **MINOR** (0.X.0) - New features, backward-compatible
- **PATCH** (0.0.X) - Bug fixes, backward-compatible

[Unreleased]: https://github.com/yourusername/axis-backend/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/yourusername/axis-backend/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/yourusername/axis-backend/releases/tag/v0.1.0
