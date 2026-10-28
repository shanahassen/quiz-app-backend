package com.example.quiz_app.repository;

import com.example.quiz_app.model.QuizAttempt;
<<<<<<< Updated upstream
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
import com.example.quiz_app.model.QuizAttemptStatus;
>>>>>>> Stashed changes
import org.springframework.data.domain.Sort;
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

    List<QuizAttempt> findByParticipantRefId(
            String participantRefId,
            Sort sort
    );

    Page<QuizAttempt> findByParticipantRefId(
            String participantRefId,
            Pageable pageable
    );

    long countByQuizRefIdAndStatus(
            String quizRefId,
            QuizAttemptStatus status
    );

<<<<<<< Updated upstream
    List<QuizAttempt> findByStatusAndDeadlineAtLessThanEqual(
            QuizAttemptStatus status,
            java.time.LocalDateTime deadlineAt
=======
    /**
     * Retrieve attempts with the specified status, newest first.
     * Archive filtering is performed in the service so older
     * MongoDB records without an archived field are handled safely.
     */
    List<QuizAttempt> findByStatusOrderByStartedAtDesc(
            QuizAttemptStatus status
>>>>>>> Stashed changes
    );
}