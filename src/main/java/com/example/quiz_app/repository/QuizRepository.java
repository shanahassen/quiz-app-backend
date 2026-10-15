package com.example.quiz_app.repository;

import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.QuizStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends MongoRepository<Quiz, String> {

    Optional<Quiz> findByRefId(String refId);

    List<Quiz> findByCategoryRefIdAndStatus(
            String categoryRefId,
            QuizStatus status
    );

    List<Quiz> findByStatus(QuizStatus status);

    boolean existsByTitleIgnoreCaseAndCategoryRefIdAndStatusNot(
            String title,
            String categoryRefId,
            QuizStatus status
    );

    boolean existsByTitleIgnoreCaseAndCategoryRefIdAndStatusNotAndRefIdNot(
            String title,
            String categoryRefId,
            QuizStatus status,
            String refId
    );
}
