package com.axis.notification.service;

import com.axis.notification.model.dto.NotificationSettingsRequest;
import com.axis.notification.model.dto.NotificationSettingsResponse;

public interface NotificationSettingsService {

    /**
     * Create or update notification settings for the current user
     */
    NotificationSettingsResponse createOrUpdate(NotificationSettingsRequest request);

    /**
     * Get notification settings for the current user
     * Creates default settings if none exist
     */
    NotificationSettingsResponse getOrCreateForCurrentUser();

    /**
     * Delete notification settings for the current user
     */
    void deleteForCurrentUser();
}