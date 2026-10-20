package com.example.quiz_app.repository;

import com.example.quiz_app.model.SystemReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface SystemReportRepository
        extends MongoRepository<SystemReport, String> {

    List<SystemReport> findAllByOrderByGeneratedAtDesc();

    Optional<SystemReport> findByRefId(String refId);
}