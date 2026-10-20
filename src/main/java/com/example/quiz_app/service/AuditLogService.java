package com.example.quiz_app.service;

import com.example.quiz_app.model.AuditLog;
import com.example.quiz_app.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void logAction(
            String actorEmail,
            String action,
            String entityType,
            String entityRefId,
            String description) {

        AuditLog log = AuditLog.builder()
                .refId(UUID.randomUUID().toString())
                .actorEmail(actorEmail)
                .action(action)
                .entityType(entityType)
                .entityRefId(entityRefId)
                .description(description)
                .timestamp(Instant.now())
                .build();

        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}