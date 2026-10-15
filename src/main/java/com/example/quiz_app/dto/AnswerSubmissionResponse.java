package com.example.quiz_app.dto;

public record AnswerSubmissionResponse(
        String attemptRefId,
        String questionRefId,
        int answeredQuestions,
        int totalQuestions
) {}