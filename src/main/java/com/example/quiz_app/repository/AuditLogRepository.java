package com.example.quiz_app.repository;

import com.example.quiz_app.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AuditLogRepository
        extends MongoRepository<AuditLog, String> {

    List<AuditLog> findAllByOrderByTimestampDesc();

    List<AuditLog> findTop3ByEntityTypeOrderByTimestampDesc(String entityType);
}