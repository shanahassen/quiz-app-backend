package com.example.quiz_app.dto;

import java.time.LocalDateTime;

public record NewestUserResponse(
        String refId,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
) {
}
