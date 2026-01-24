package com.axis.notification.repository;

import com.axis.notification.model.entity.NotificationTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplatesRepository extends JpaRepository<NotificationTemplates, UUID> {

    /**
     * Find notification template by type
     */
    Optional<NotificationTemplates> findByType(NotificationTemplates.Type type);

    /**
     * Check if a template with the given type already exists
     */
    boolean existsByType(NotificationTemplates.Type type);

    /**
     * Find all templates with pagination
     */
    Page<NotificationTemplates> findAll(Pageable pageable);
}