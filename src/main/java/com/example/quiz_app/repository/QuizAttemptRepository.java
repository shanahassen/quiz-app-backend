package com.example.quiz_app.repository;

import com.example.quiz_app.model.QuizAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository
        extends MongoRepository<QuizAttempt, String> {

    Optional<QuizAttempt> findByRefId(String refId);

    boolean existsByQuizRefId(String quizRefId);

    Optional<QuizAttempt> findByRefIdAndParticipantRefId(
            String refId,
            String participantRefId
    );

    List<QuizAttempt> findByParticipantRefId(
            String participantRefId
    );

    List<QuizAttempt> findByParticipantRefIdAndQuizRefId(
            String participantRefId,
            String quizRefId
    );
}
