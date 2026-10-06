package com.example.quiz_app.repository;

import com.example.quiz_app.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission, String> {
}
