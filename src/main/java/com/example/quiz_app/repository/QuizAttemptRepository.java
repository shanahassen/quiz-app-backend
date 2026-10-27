package com.example.quiz_app.repository;

import com.example.quiz_app.model.QuizAttempt;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
import com.example.quiz_app.model.QuizAttemptStatus;

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

    List<QuizAttempt> findByParticipantRefId(
            String participantRefId,
            Sort sort
    );

    long countByQuizRefIdAndStatus(
            String quizRefId,
            QuizAttemptStatus status
    );

    List<QuizAttempt> findByStatusAndDeadlineAtLessThanEqual(
            QuizAttemptStatus status,
            java.time.LocalDateTime deadlineAt
    );
}