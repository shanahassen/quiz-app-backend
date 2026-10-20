package com.example.quiz_app.dto;

import java.time.Instant;
import java.util.Map;

public record SystemReportResponse(
        String refId,
        String reportName,
        String generatedBy,
        Instant generatedAt,
        Map<String, Long> summary
) {
}