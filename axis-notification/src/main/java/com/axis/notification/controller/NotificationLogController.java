package com.axis.notification.controller;

import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;
import com.axis.notification.service.NotificationLogService;
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
@RequestMapping("/api/notifications/logs")
@RequiredArgsConstructor
@Tag(name = "Notification Logs", description = "Endpoints for managing user notification logs")
public class NotificationLogController {

    private final NotificationLogService service;

    @PostMapping
    @Operation(summary = "Create notification log", description = "Creates a new notification log entry for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<NotificationLogResponse> create(
            @Valid @RequestBody NotificationLogRequest request) {
        log.info("Received request to create notification log with channel: {}", request.channel());
        NotificationLogResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a notification log entry by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "403", description = "Access denied to notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationLogResponse> findById(
            @Parameter(description = "Notification ID") @PathVariable UUID id) {
        log.info("Received request to find notification with id: {}", id);
        NotificationLogResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List user notifications", description = "Retrieves all notifications for the current user with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Page<NotificationLogResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("Received request to find all notifications with pagination: {}", pageable);
        Page<NotificationLogResponse> response = service.findByCurrentUser(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List notifications by status", description = "Retrieves notifications filtered by status for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Page<NotificationLogResponse>> findByStatus(
            @Parameter(description = "Notification status") @PathVariable NotificationLog.Status status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("Received request to find notifications with status: {}", status);
        Page<NotificationLogResponse> response = service.findByCurrentUserAndStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/channel/{channel}")
    @Operation(summary = "List notifications by channel", description = "Retrieves notifications filtered by channel for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Page<NotificationLogResponse>> findByChannel(
            @Parameter(description = "Notification channel") @PathVariable NotificationLog.Channel channel,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("Received request to find notifications with channel: {}", channel);
        Page<NotificationLogResponse> response = service.findByCurrentUserAndChannel(channel, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count unread notifications", description = "Returns the count of unread notifications for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Long> countUnread() {
        log.info("Received request to count unread notifications");
        long count = service.countUnread();
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update notification status", description = "Updates the status of a notification (e.g., mark as read)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationLogResponse> updateStatus(
            @Parameter(description = "Notification ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam NotificationLog.Status status) {
        log.info("Received request to update notification {} status to {}", id, status);
        NotificationLogResponse response = service.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Deletes a notification by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied to notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Notification ID") @PathVariable UUID id) {
        log.info("Received request to delete notification with id: {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete all notifications", description = "Deletes all notifications for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All notifications deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<Void> deleteAll() {
        log.info("Received request to delete all notifications for current user");
        service.deleteByCurrentUser();
        return ResponseEntity.noContent().build();
    }
}