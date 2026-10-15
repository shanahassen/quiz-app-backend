package com.example.quiz_app.repository;

import com.example.quiz_app.model.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository
        extends MongoRepository<Certificate, String> {

    Optional<Certificate> findByAttemptRefId(String attemptRefId);

    List<Certificate> findByParticipantRefIdOrderByIssuedAtDesc(
            String participantRefId);

    Optional<Certificate> findByRefIdAndParticipantRefId(
            String refId,
            String participantRefId);
}