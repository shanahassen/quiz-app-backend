package com.example.quiz_app.dto;

import com.example.quiz_app.model.QuizAttemptStatus;

import java.time.LocalDateTime;

public record QuizAttemptHistoryResponse(
        String refId,
        String quizRefId,
        String quizTitle,
        QuizAttemptStatus status,
        int answeredQuestions,
        int totalQuestions,
        Integer score,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        boolean archived
) {
}