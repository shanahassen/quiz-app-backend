package com.example.quiz_app.repository;

import com.example.quiz_app.model.User;
import com.example.quiz_app.model.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

<<<<<<< Updated upstream
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}
=======
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndUserStatus(String email, UserStatus status);
}
>>>>>>> Stashed changes
