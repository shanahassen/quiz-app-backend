package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "certificates")
public class Certificate extends BaseEntity {

    @Indexed(unique = true)
    private String refId;

    @Indexed(unique = true)
    private String attemptRefId;

    private String quizRefId;

    private String participantRefId;

    private String participantName;

    private String quizTitle;

    private Integer score;

    private Integer totalQuestions;

    private double percentage;

    private RatingLevel ratingLevel;

    private LocalDateTime issuedAt;
}