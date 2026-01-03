package com.axis.goal.model.dto;

import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.model.entity.Goal.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO containing goal information")
public record GoalResponse(

    @Schema(description = "Unique identifier of the goal")
    UUID id,

    @Schema(description = "Title of the goal", example = "Learn Spring Boot")
    String title,

    @Schema(description = "Detailed description of the goal", example = "Master Spring Boot 3 and build microservices")
    String description,

    @Schema(description = "Type of the goal", example = "MEDIUM_TERM")
    GoalType type,

    @Schema(description = "Current status of the goal", example = "IN_PROGRESS")
    GoalStatus status,

    @Schema(description = "Start date of the goal", example = "2025-01-01")
    LocalDate startDate,

    @Schema(description = "Deadline for completing the goal", example = "2025-12-31")
    LocalDate deadline,

    @Schema(description = "Date when the goal was completed", example = "2025-11-15")
    LocalDate completionDate,

    @Schema(description = "ID of the user who owns this goal")
    UUID userId,

    @Schema(description = "Timestamp when the goal was created")
    LocalDateTime createdAt,

    @Schema(description = "Timestamp when the goal was last updated")
    LocalDateTime updatedAt

) {
}