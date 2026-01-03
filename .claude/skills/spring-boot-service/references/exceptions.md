# Exception Handling

## Global Exception Handler (from axis-common)

The platform uses a centralized exception handler in the `axis-common` module that all microservices inherit.

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.warn("Business exception: {}", ex.getMessage());

        HttpStatus status = ex.getStatus();
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError ?
                            ((FieldError) error).getField() : error.getObjectName();
                    String message = error.getDefaultMessage();
                    Object rejectedValue = error instanceof FieldError ?
                            ((FieldError) error).getRejectedValue() : null;

                    return ApiError.FieldError.builder()
                            .field(fieldName)
                            .message(message)
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .path(extractPath(request))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({AuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ApiError> handleAuthenticationException(
            Exception ex, WebRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Authentication failed")
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Access denied")
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
```

## ApiError DTO (from axis-common)

Standardized error response structure used across all microservices.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
```

### Example Response

**Validation Error (400):**
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/goals",
  "fieldErrors": [
    {
      "field": "title",
      "message": "must not be blank",
      "rejectedValue": null
    },
    {
      "field": "goalType",
      "message": "must be one of: LONG_TERM, MEDIUM_TERM, SHORT_TERM",
      "rejectedValue": "INVALID_TYPE"
    }
  ]
}
```

**Not Found Error (404):**
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Goal not found with id: 123e4567-e89b-12d3-a456-426614174000",
  "path": "/api/goals/123e4567-e89b-12d3-a456-426614174000",
  "fieldErrors": null
}
```

## Custom Exception Pattern

### ResourceNotFoundException

Used for 404 Not Found scenarios.

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Usage
throw new ResourceNotFoundException("Goal not found with id: " + id);
```

### BusinessException

Used for business logic violations with configurable HTTP status.

```java
@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
}

// Usage
throw new BusinessException("Goal with this title already exists", HttpStatus.CONFLICT);
throw new BusinessException("Invalid operation");  // Defaults to BAD_REQUEST
```

### Service-Specific Exceptions

Create domain-specific exceptions that extend base exceptions:

```java
public class GoalNotFoundException extends ResourceNotFoundException {
    public GoalNotFoundException(UUID id) {
        super("Goal not found with id: " + id);
    }
}

public class InvalidGoalStatusException extends BusinessException {
    public InvalidGoalStatusException(String currentStatus, String targetStatus) {
        super("Cannot transition from " + currentStatus + " to " + targetStatus,
              HttpStatus.BAD_REQUEST);
    }
}
```

## Usage in Services

```java
@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    @Override
    public GoalResponseDTO findById(UUID id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        return goalMapper.toResponseDTO(goal);
    }

    @Override
    public GoalResponseDTO create(GoalRequestDTO request) {
        // Extract user from security context
        UUID userId = SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated",
                                                         HttpStatus.UNAUTHORIZED));

        Goal goal = goalMapper.toEntity(request);
        goal.setUserId(userId);
        goal = goalRepository.save(goal);

        return goalMapper.toResponseDTO(goal);
    }
}
```

## Important Notes

- **Automatic Handling:** All services that depend on `axis-common` get global exception handling automatically
- **No Need to Re-declare:** Don't create your own `@RestControllerAdvice` in individual services
- **Consistent Responses:** All errors follow the same `ApiError` structure across all microservices
- **Logging:** Errors are automatically logged at appropriate levels (warn for client errors, error for server errors)
- **Security:** Generic error messages prevent information leakage in production
- **User Context:** Use `SecurityUtils` to extract authenticated user information from JWT tokens