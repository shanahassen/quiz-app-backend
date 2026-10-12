package com.example.quiz_app.repository;

import com.example.quiz_app.model.User;
import com.example.quiz_app.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailAndStatus(String email, Status status);

    Optional<User> findByEmailIgnoreCaseAndStatus(String email, Status status);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByRefId(String refId);
}
