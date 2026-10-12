package com.example.quiz_app.repository;

import com.example.quiz_app.model.Category;
import com.example.quiz_app.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    boolean existsByNameIgnoreCaseAndStatus(String name, Status status);

    boolean existsByNameIgnoreCaseAndStatusAndRefIdNot(String name, Status status, String refId);

    Optional<Category> findByRefId(String refId);

    List<Category> findByStatus(Status status);
}
