package com.axis.notification.service;

import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationLogService {

    /**
     * Create a new notification log entry for the current user
     */
    NotificationLogResponse create(NotificationLogRequest request);

    /**
     * Find notification by ID (must belong to current user)
     */
    NotificationLogResponse findById(UUID id);

    /**
     * Find all notifications for the current user
     */
    Page<NotificationLogResponse> findByCurrentUser(Pageable pageable);

    /**
     * Find notifications by current user and status
     */
    Page<NotificationLogResponse> findByCurrentUserAndStatus(NotificationLog.Status status, Pageable pageable);

    /**
     * Find notifications by current user and channel
     */
    Page<NotificationLogResponse> findByCurrentUserAndChannel(NotificationLog.Channel channel, Pageable pageable);

    /**
     * Update notification status (e.g., mark as read)
     */
    NotificationLogResponse updateStatus(UUID id, NotificationLog.Status status);

    /**
     * Delete notification by ID (must belong to current user)
     */
    void deleteById(UUID id);

    /**
     * Delete all notifications for the current user
     */
    void deleteByCurrentUser();

    /**
     * Count unread notifications for the current user
     */
    long countUnread();
}