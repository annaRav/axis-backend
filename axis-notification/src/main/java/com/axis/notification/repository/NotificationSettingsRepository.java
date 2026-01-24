package com.axis.notification.repository;

import com.axis.notification.model.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {

    /**
     * Find notification settings by user ID
     */
    Optional<NotificationSettings> findByUserId(UUID userId);

    /**
     * Check if notification settings exist for a user
     */
    boolean existsByUserId(UUID userId);

    /**
     * Delete notification settings for a specific user
     */
    void deleteByUserId(UUID userId);
}