package com.example.quiz_app.repository;

import com.example.quiz_app.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PermissionRepository extends MongoRepository<Permission, String> {

    Optional<Permission> findByRefId(String refId);
}
