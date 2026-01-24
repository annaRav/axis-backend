package com.axis.notification.service.pg;

import com.axis.notification.mapper.NotificationSettingsMapper;
import com.axis.notification.model.dto.NotificationSettingsRequest;
import com.axis.notification.model.dto.NotificationSettingsResponse;
import com.axis.notification.model.entity.NotificationSettings;
import com.axis.notification.repository.NotificationSettingsRepository;
import com.axis.notification.service.NotificationSettingsService;
import com.axis.common.exception.BusinessException;
import com.axis.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationSettingsServicePg implements NotificationSettingsService {

    private final NotificationSettingsRepository repository;
    private final NotificationSettingsMapper mapper;

    @Override
    @Transactional
    public NotificationSettingsResponse createOrUpdate(NotificationSettingsRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Creating or updating notification settings for user: {}", currentUserId);

        NotificationSettings entity = repository.findByUserId(currentUserId)
                .map(existing -> {
                    log.debug("Updating existing notification settings for user: {}", currentUserId);
                    mapper.updateEntity(request, existing);
                    return existing;
                })
                .orElseGet(() -> {
                    log.debug("Creating new notification settings for user: {}", currentUserId);
                    NotificationSettings newEntity = mapper.toEntity(request);
                    newEntity.setUserId(currentUserId);
                    return newEntity;
                });

        NotificationSettings saved = repository.save(entity);
        log.info("Saved notification settings for user: {}", currentUserId);

        return mapper.toResponse(saved);
    }

    @Override
    public NotificationSettingsResponse getOrCreateForCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notification settings for user: {}", currentUserId);

        NotificationSettings entity = repository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    log.debug("No settings found for user: {}, returning defaults", currentUserId);
                    return createDefaultSettings(currentUserId);
                });

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteForCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Deleting notification settings for user: {}", currentUserId);

        repository.deleteByUserId(currentUserId);
        log.info("Deleted notification settings for user: {}", currentUserId);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", HttpStatus.UNAUTHORIZED));
    }

    private NotificationSettings createDefaultSettings(UUID userId) {
        log.debug("Creating default notification settings for user: {}", userId);
        return NotificationSettings.builder()
                .userId(userId)
                .enableEmail(true)
                .enablePush(true)
                .enableTelegram(false)
                .build();
    }
}