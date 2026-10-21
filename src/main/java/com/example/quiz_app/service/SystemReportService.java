package com.example.quiz_app.service;

import com.example.quiz_app.dto.SystemReportResponse;
import com.example.quiz_app.model.SystemReport;
import com.example.quiz_app.repository.SystemReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemReportService {

    private final MongoTemplate mongoTemplate;
    private final SystemReportRepository reportRepository;

    public SystemReportResponse generateReport(String generatedBy) {

        Map<String, Long> summary = new LinkedHashMap<>();

        summary.put("Users", countCollections("user"));
        summary.put("Categories", countCollections("categor"));
        summary.put("Quizzes", countCollections("quiz"));
        summary.put("Quiz Attempts", countCollections("attempt"));
        summary.put("Certificates", countCollections("certificate"));

        SystemReport report = SystemReport.builder()
                .refId(UUID.randomUUID().toString())
                .reportName("System Summary Report")
                .generatedBy(generatedBy)
                .generatedAt(Instant.now())
                .summary(summary)
                .build();

        return toResponse(reportRepository.save(report));
    }

    private long countCollections(String keyword) {

        List<String> matchingCollections =
                mongoTemplate.getCollectionNames()
                        .stream()
                        .filter(name -> name.toLowerCase(Locale.ROOT)
                                .contains(keyword))
                        .filter(name -> !name.equals("audit_logs"))
                        .filter(name -> !name.equals("system_reports"))
                        .toList();

        return matchingCollections.stream()
                .mapToLong(name ->
                        mongoTemplate.getCollection(name).countDocuments())
                .sum();
    }

    public List<SystemReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByGeneratedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SystemReportResponse getReport(String refId) {
        return reportRepository.findByRefId(refId)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "System report not found: " + refId));
    }

    private SystemReportResponse toResponse(SystemReport report) {
        return new SystemReportResponse(
                report.getRefId(),
                report.getReportName(),
                report.getGeneratedBy(),
                report.getGeneratedAt(),
                report.getSummary()
        );
    }
}