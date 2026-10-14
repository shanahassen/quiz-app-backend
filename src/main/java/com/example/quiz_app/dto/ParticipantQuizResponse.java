package com.example.quiz_app.dto;

import com.example.quiz_app.model.QuizStatus;

import java.util.List;

public record ParticipantQuizResponse(
        String refId,
        String categoryRefId,
        String title,
        String description,
        QuizStatus status,
        List<QuestionResponse> questions
) {
    public record QuestionResponse(
            String refId,
            String text,
            List<OptionResponse> options
    ) {}

    public record OptionResponse(
            String refId,
            String text
    ) {}
}
