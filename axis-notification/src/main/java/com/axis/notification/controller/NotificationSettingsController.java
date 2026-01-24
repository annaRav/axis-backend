package com.axis.notification.controller;

import com.axis.notification.model.dto.NotificationSettingsRequest;
import com.axis.notification.model.dto.NotificationSettingsResponse;
import com.axis.notification.service.NotificationSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notifications/settings")
@RequiredArgsConstructor
@Tag(name = "Notification Settings", description = "Endpoints for managing user notification preferences")
public class NotificationSettingsController {

    private final NotificationSettingsService service;

    @PutMapping
    @Operation(summary = "Create or update notification settings",
            description = "Creates or updates notification preferences for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Settings saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<NotificationSettingsResponse> createOrUpdate(
            @Valid @RequestBody NotificationSettingsRequest request) {
        log.info("Received request to create or update notification settings");
        NotificationSettingsResponse response = service.createOrUpdate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get notification settings",
            description = "Retrieves notification preferences for the current user (returns defaults if none exist)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<NotificationSettingsResponse> getOrCreateForCurrentUser() {
        log.info("Received request to find notification settings for current user");
        NotificationSettingsResponse response = service.getOrCreateForCurrentUser();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Delete notification settings",
            description = "Deletes notification preferences for the current user (will revert to defaults)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Settings deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Void> deleteForCurrentUser() {
        log.info("Received request to delete notification settings for current user");
        service.deleteForCurrentUser();
        return ResponseEntity.noContent().build();
    }
}