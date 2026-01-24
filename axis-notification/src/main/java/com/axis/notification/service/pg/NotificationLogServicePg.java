package com.axis.notification.service.pg;

import com.axis.notification.mapper.NotificationLogMapper;
import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;
import com.axis.notification.repository.NotificationLogRepository;
import com.axis.notification.service.NotificationLogService;
import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationLogServicePg implements NotificationLogService {

    private final NotificationLogRepository repository;
    private final NotificationLogMapper mapper;

    @Override
    @Transactional
    public NotificationLogResponse create(NotificationLogRequest request) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Creating notification log for user: {} with channel: {}", currentUserId, request.channel());

        NotificationLog entity = mapper.toEntity(request);
        entity.setUserId(currentUserId);

        // Set default status if not provided
        if (entity.getStatus() == null) {
            entity.setStatus(NotificationLog.Status.SENT);
        }

        NotificationLog saved = repository.save(entity);
        log.info("Created notification log with id: {} for user: {}", saved.getId(), currentUserId);

        return mapper.toResponse(saved);
    }

    @Override
    public NotificationLogResponse findById(UUID id) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notification log by id: {} for user: {}", id, currentUserId);

        NotificationLog entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Ensure user can only access their own notifications
        validateOwnership(entity, currentUserId);

        return mapper.toResponse(entity);
    }

    @Override
    public Page<NotificationLogResponse> findByCurrentUser(Pageable pageable) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding all notifications for user: {} with pagination: {}", currentUserId, pageable);

        Page<NotificationLog> page = repository.findByUserId(currentUserId, pageable);
        log.debug("Found {} notifications for user: {}", page.getTotalElements(), currentUserId);

        return page.map(mapper::toResponse);
    }

    @Override
    public Page<NotificationLogResponse> findByCurrentUserAndStatus(NotificationLog.Status status, Pageable pageable) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notifications for user: {} with status: {} and pagination: {}",
                currentUserId, status, pageable);

        Page<NotificationLog> page = repository.findByUserIdAndStatus(currentUserId, status, pageable);
        log.debug("Found {} notifications with status {} for user: {}",
                page.getTotalElements(), status, currentUserId);

        return page.map(mapper::toResponse);
    }

    @Override
    public Page<NotificationLogResponse> findByCurrentUserAndChannel(NotificationLog.Channel channel, Pageable pageable) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Finding notifications for user: {} with channel: {} and pagination: {}",
                currentUserId, channel, pageable);

        Page<NotificationLog> page = repository.findByUserIdAndChannel(currentUserId, channel, pageable);
        log.debug("Found {} notifications with channel {} for user: {}",
                page.getTotalElements(), channel, currentUserId);

        return page.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public NotificationLogResponse updateStatus(UUID id, NotificationLog.Status status) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Updating notification {} status to {} for user: {}", id, status, currentUserId);

        NotificationLog entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Ensure user can only update their own notifications
        validateOwnership(entity, currentUserId);

        entity.setStatus(status);
        NotificationLog updated = repository.save(entity);

        log.info("Updated notification {} status to {} for user: {}", id, status, currentUserId);
        return mapper.toResponse(updated);
    }

    @Override
    public long countUnread() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Counting unread notifications for user: {}", currentUserId);

        long count = repository.countByUserIdAndStatus(currentUserId, NotificationLog.Status.SENT);
        log.debug("User {} has {} unread notifications", currentUserId, count);

        return count;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UUID currentUserId = getCurrentUserId();
        log.debug("Deleting notification {} for user: {}", id, currentUserId);

        NotificationLog entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Ensure user can only delete their own notifications
        validateOwnership(entity, currentUserId);

        repository.deleteById(id);
        log.info("Deleted notification {} for user: {}", id, currentUserId);
    }

    @Override
    @Transactional
    public void deleteByCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        log.debug("Deleting all notifications for user: {}", currentUserId);

        repository.deleteByUserId(currentUserId);
        log.info("Deleted all notifications for user: {}", currentUserId);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new BusinessException("User not authenticated", HttpStatus.UNAUTHORIZED));
    }

    private void validateOwnership(NotificationLog entity, UUID currentUserId) {
        if (!entity.getUserId().equals(currentUserId)) {
            throw new BusinessException("Access denied to notification", HttpStatus.FORBIDDEN);
        }
    }
}