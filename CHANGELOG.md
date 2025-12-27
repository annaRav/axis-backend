# Changelog

All notable changes to the Wiki Backend project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Created `.claudeignore` for performance optimization
- Added maintenance documentation section to CLAUDE.md

### Changed
- Updated CLAUDE.md with instructions for maintaining project documentation

## [0.2.0] - 2024-12-21

### Added
- **wiki-media** microservice for media management
- MCP (Model Context Protocol) integration for database access
- Agent-based development tooling

### Changed
- Renamed Organization microservice architecture
- Refactored role management system

### Fixed
- Skaffold startup configuration issues
- Development environment initialization

## [0.1.0] - 2024-12-06

### Added
- **wiki-gateway** microservice (Spring Cloud Gateway with WebFlux)
- **wiki-membership** microservice for organization and membership management
- **wiki-common** shared library (security, exceptions, DTOs)
- Keycloak integration for OAuth2/OIDC authentication
- PostgreSQL databases (separate for Keycloak and application)
- MongoDB integration
- RabbitMQ message broker
- Redis caching
- Kubernetes deployment configuration (Minikube)
- Skaffold for local development
- Flyway database migrations
- MapStruct for DTO conversions
- Global exception handling
- JWT authentication with custom converter
- Security utilities for user context access
- Actuator endpoints for health and metrics
- OpenAPI/Swagger documentation

### Infrastructure
- Kubernetes namespace `wiki`
- PostgreSQL for Keycloak (port 5434)
- PostgreSQL for application data (port 5433)
- Keycloak realm `wiki` with test users
- Gateway routing configuration
- Docker image builds via Jib

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
- New article management endpoints in wiki-content service
- Support for markdown rendering

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

[Unreleased]: https://github.com/yourusername/wiki-backend/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/yourusername/wiki-backend/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/yourusername/wiki-backend/releases/tag/v0.1.0