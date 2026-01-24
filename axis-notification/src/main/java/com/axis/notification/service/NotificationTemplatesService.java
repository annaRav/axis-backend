package com.axis.notification.service;

import com.axis.notification.model.dto.NotificationTemplateRequest;
import com.axis.notification.model.dto.NotificationTemplateResponse;
import com.axis.notification.model.entity.NotificationTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationTemplatesService {

    /**
     * Create a new notification template
     */
    NotificationTemplateResponse create(NotificationTemplateRequest request);

    /**
     * Update an existing notification template
     */
    NotificationTemplateResponse update(UUID id, NotificationTemplateRequest request);

    /**
     * Find notification template by ID
     */
    NotificationTemplateResponse findById(UUID id);

    /**
     * Find notification template by type
     */
    NotificationTemplateResponse findByType(NotificationTemplates.Type type);

    /**
     * Find all notification templates with pagination
     */
    Page<NotificationTemplateResponse> findAll(Pageable pageable);

    /**
     * Delete notification template by ID
     */
    void deleteById(UUID id);
}