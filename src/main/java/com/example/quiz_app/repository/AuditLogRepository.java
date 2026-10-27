package com.example.quiz_app.repository;

import com.example.quiz_app.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AuditLogRepository
        extends MongoRepository<AuditLog, String> {

    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

    List<AuditLog> findAllByOrderByTimestampDesc();

    List<AuditLog> findTop3ByEntityTypeOrderByTimestampDesc(String entityType);
}