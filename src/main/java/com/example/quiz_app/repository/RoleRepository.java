package com.example.quiz_app.repository;

import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.RoleName;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByRoleName(RoleName roleName);
}