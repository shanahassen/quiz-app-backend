package com.example.quiz_app.repository;

import com.example.quiz_app.model.Status;
import com.example.quiz_app.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailAndStatus(String email, Status status);

    Optional<User> findByEmailIgnoreCaseAndStatus(String email, Status status);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByRefId(String refId);

    List<User> findTop3ByOrderByCreatedAtDesc();
    List<User> findTop3ByRoleIdOrderByCreatedAtDesc(String roleId);

    // Dashboard statistics
    long countByStatus(Status status);
}
