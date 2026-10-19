
package com.example.quiz_app.dto;

import java.util.List;

public record QuizRequest(
        String categoryRefId,
        String title,
        String description,
        List<QuestionRequest> questions
) {
    public record QuestionRequest(
            String refId,
            String text,
            String hint,
            List<AnswerOptionRequest> options
    ) {}

    public record AnswerOptionRequest(
            String refId,
            String text,
            boolean correct
    ) {}
}
