package com.example.quiz_app.repository;

import com.example.quiz_app.model.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository
        extends MongoRepository<Certificate, String> {

    Optional<Certificate> findByAttemptRefId(String attemptRefId);

    Page<Certificate> findByParticipantRefIdOrderByIssuedAtDesc(
            String participantRefId,
            Pageable pageable);

    Optional<Certificate> findByRefIdAndParticipantRefId(
            String refId,
            String participantRefId);
}