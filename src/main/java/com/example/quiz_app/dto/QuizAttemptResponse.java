package com.example.quiz_app.dto;

import com.example.quiz_app.model.QuizAttemptStatus;
import java.time.LocalDateTime;

public record QuizAttemptResponse(
        String refId,
        String quizRefId,
        QuizAttemptStatus status,
        int answeredQuestions,
        int totalQuestions,
        Integer score,
        LocalDateTime startedAt,
        LocalDateTime deadlineAt,
        LocalDateTime submittedAt
) {
}