package com.example.quiz_app.dto;

public record AnswerFeedbackResponse(
        String attemptRefId,
        String questionRefId,
        String selectedOptionRefId,
        int answeredQuestions,
        int totalQuestions
) {
}
