package com.axis.notification.repository;

import com.axis.notification.model.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    /**
     * Find all notifications for a specific user with pagination
     */
    Page<NotificationLog> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find notifications by user ID and status with pagination
     */
    Page<NotificationLog> findByUserIdAndStatus(UUID userId, NotificationLog.Status status, Pageable pageable);

    /**
     * Find notifications by user ID, channel, and status
     */
    List<NotificationLog> findByUserIdAndChannelAndStatus(UUID userId, NotificationLog.Channel channel, NotificationLog.Status status);

    /**
     * Find notifications by user ID and channel with pagination
     */
    Page<NotificationLog> findByUserIdAndChannel(UUID userId, NotificationLog.Channel channel, Pageable pageable);

    /**
     * Delete all notifications for a specific user
     */
    void deleteByUserId(UUID userId);

    /**
     * Count unread notifications for a user
     */
    long countByUserIdAndStatus(UUID userId, NotificationLog.Status status);
}