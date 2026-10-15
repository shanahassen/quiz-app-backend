package com.example.quiz_app.dto;

import com.example.quiz_app.model.RatingLevel;
import java.time.LocalDateTime;

public record CertificateResponse(
        String refId,
        String attemptRefId,
        String quizRefId,
        String quizTitle,
        String participantName,
        Integer score,
        Integer totalQuestions,
        double percentage,
        RatingLevel ratingLevel,
        LocalDateTime issuedAt
) {
}