package com.axis.notification.controller;

import com.axis.notification.model.dto.NotificationTemplateRequest;
import com.axis.notification.model.dto.NotificationTemplateResponse;
import com.axis.notification.model.entity.NotificationTemplates;
import com.axis.notification.service.NotificationTemplatesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/notifications/templates")
@RequiredArgsConstructor
@Tag(name = "Notification Templates", description = "Endpoints for managing notification templates")
public class NotificationTemplatesController {

    private final NotificationTemplatesService service;

    @PostMapping
    @Operation(summary = "Create notification template", description = "Creates a new notification template (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Template created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Template with this type already exists")
    })
    public ResponseEntity<NotificationTemplateResponse> create(
            @Valid @RequestBody NotificationTemplateRequest request) {
        log.info("Received request to create notification template with type: {}", request.type());
        NotificationTemplateResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update notification template", description = "Updates an existing notification template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "409", description = "Another template with this type already exists")
    })
    public ResponseEntity<NotificationTemplateResponse> update(
            @Parameter(description = "Template ID") @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplateRequest request) {
        log.info("Received request to update notification template with id: {}", id);
        NotificationTemplateResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification template by ID", description = "Retrieves a notification template by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<NotificationTemplateResponse> findById(
            @Parameter(description = "Template ID") @PathVariable UUID id) {
        log.info("Received request to find notification template with id: {}", id);
        NotificationTemplateResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get notification template by type", description = "Retrieves a notification template by its type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<NotificationTemplateResponse> findByType(
            @Parameter(description = "Template type") @PathVariable NotificationTemplates.Type type) {
        log.info("Received request to find notification template with type: {}", type);
        NotificationTemplateResponse response = service.findByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all notification templates", description = "Retrieves all notification templates with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    })
    public ResponseEntity<Page<NotificationTemplateResponse>> findAll(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to find all notification templates with pagination: {}", pageable);
        Page<NotificationTemplateResponse> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification template", description = "Deletes a notification template by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Template deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Template ID") @PathVariable UUID id) {
        log.info("Received request to delete notification template with id: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}