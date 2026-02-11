package com.axis.goal.model.dto;

import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request DTO for creating or updating a goal type (layer configuration)")
public record GoalTypeRequest(

        @Schema(description = "Title of the goal type/layer")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title
) {
}
