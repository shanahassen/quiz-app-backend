package com.example.quiz_app.dto;

import com.example.quiz_app.model.Status;
import java.time.LocalDateTime;

public record CategoryResponse(
        String refId,
        String name,
        String description,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
