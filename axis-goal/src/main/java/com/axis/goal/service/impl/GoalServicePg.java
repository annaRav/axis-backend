package com.axis.goal.service.impl;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalMapper;
import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.model.entity.Goal.GoalType;
import com.axis.goal.repository.GoalRepository;
import com.axis.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalServicePg implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Override
    @Transactional
    public GoalResponse create(GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating new goal for user: {}", userId);

        Goal goal = goalMapper.toEntity(request);
        goal.setUserId(userId);

        Goal saved = goalRepository.save(goal);
        log.info("Created goal with id: {} for user: {}", saved.getId(), userId);

        return goalMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GoalResponse update(UUID id, GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating goal: {} for user: {}", id, userId);

        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        goalMapper.updateEntity(request, goal);

        Goal updated = goalRepository.save(goal);
        log.info("Updated goal: {} for user: {}", id, userId);

        return goalMapper.toResponse(updated);
    }

    @Override
    public GoalResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goal: {} for user: {}", id, userId);

        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        return goalMapper.toResponse(goal);
    }

    @Override
    public Page<GoalResponse> findAll(Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Finding all goals for user: {}", userId);

        return goalRepository.findByUserId(userId, pageable)
                .map(goalMapper::toResponse);
    }

    @Override
    public Page<GoalResponse> findByStatus(GoalStatus status, Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goals with status: {} for user: {}", status, userId);

        return goalRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(goalMapper::toResponse);
    }

    @Override
    public Page<GoalResponse> findByType(GoalType type, Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goals with type: {} for user: {}", type, userId);

        return goalRepository.findByUserIdAndType(userId, type, pageable)
                .map(goalMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting goal: {} for user: {}", id, userId);

        if (!goalRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Goal", id);
        }

        goalRepository.deleteByIdAndUserId(id, userId);
        log.info("Deleted goal: {} for user: {}", id, userId);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}