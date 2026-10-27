package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "quizzes")
public class Quiz extends BaseEntity {

    @Indexed(unique = true)
    private String refId;
    private String categoryRefId;
    private String title;
    private String description;
    private Integer durationMinutes = 30;
    private QuizStatus status = QuizStatus.DRAFT;
    private List<Question> questions = new ArrayList<>();

    @Data
    public static class Question {
        private String refId;
        private String text;
        private String hint;
        private List<AnswerOption> options = new ArrayList<>();
    }

    @Transient
    private long activeAttemptCount;

    @Data
    public static class AnswerOption {
        private String refId;
        private String text;
        private boolean correct;
    }
}