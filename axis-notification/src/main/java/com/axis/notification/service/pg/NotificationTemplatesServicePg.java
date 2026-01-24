package com.axis.notification.service.pg;

import com.axis.notification.mapper.NotificationTemplatesMapper;
import com.axis.notification.model.dto.NotificationTemplateRequest;
import com.axis.notification.model.dto.NotificationTemplateResponse;
import com.axis.notification.model.entity.NotificationTemplates;
import com.axis.notification.repository.NotificationTemplatesRepository;
import com.axis.notification.service.NotificationTemplatesService;
import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
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
public class NotificationTemplatesServicePg implements NotificationTemplatesService {

    private final NotificationTemplatesRepository repository;
    private final NotificationTemplatesMapper mapper;

    @Override
    @Transactional
    public NotificationTemplateResponse create(NotificationTemplateRequest request) {
        log.debug("Creating notification template with type: {}", request.type());

        // Check if template with this type already exists
        if (repository.existsByType(request.type())) {
            throw new BusinessException(
                    "Notification template with type " + request.type() + " already exists",
                    HttpStatus.CONFLICT
            );
        }

        NotificationTemplates entity = mapper.toEntity(request);
        NotificationTemplates saved = repository.save(entity);

        log.info("Created notification template with id: {} and type: {}", saved.getId(), saved.getType());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public NotificationTemplateResponse update(UUID id, NotificationTemplateRequest request) {
        log.debug("Updating notification template with id: {}", id);

        NotificationTemplates entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        // Check if another template with the same type exists (excluding current one)
        repository.findByType(request.type()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException(
                        "Another notification template with type " + request.type() + " already exists",
                        HttpStatus.CONFLICT
                );
            }
        });

        mapper.updateEntity(request, entity);
        NotificationTemplates updated = repository.save(entity);

        log.info("Updated notification template with id: {}", id);
        return mapper.toResponse(updated);
    }

    @Override
    public NotificationTemplateResponse findById(UUID id) {
        log.debug("Finding notification template by id: {}", id);

        NotificationTemplates entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        return mapper.toResponse(entity);
    }

    @Override
    public NotificationTemplateResponse findByType(NotificationTemplates.Type type) {
        log.debug("Finding notification template by type: {}", type);

        NotificationTemplates entity = repository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with type: " + type));

        return mapper.toResponse(entity);
    }

    @Override
    public Page<NotificationTemplateResponse> findAll(Pageable pageable) {
        log.debug("Finding all notification templates with pagination: {}", pageable);

        Page<NotificationTemplates> page = repository.findAll(pageable);
        log.debug("Found {} notification templates", page.getTotalElements());

        return page.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting notification template with id: {}", id);

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Notification template not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("Deleted notification template with id: {}", id);
    }
}