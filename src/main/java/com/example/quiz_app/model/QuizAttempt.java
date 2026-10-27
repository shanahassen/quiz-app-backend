package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "quiz_attempts")
public class QuizAttempt extends BaseEntity {

    @Indexed(unique = true)
    private String refId;
    private String quizRefId;
    private String participantRefId;
    private QuizAttemptStatus status;
    private List<Answer> answers = new ArrayList<>();
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime deadlineAt;
    private LocalDateTime submittedAt;

    @Data
    public static class Answer {

        private String questionRefId;
        private String selectedOptionRefId;
        private boolean correct;
        private LocalDateTime answeredAt;
    }
}