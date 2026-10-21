package com.example.quiz_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "system_reports")
public class SystemReport {

    @Id
    private String id;

    private String refId;
    private String reportName;
    private String generatedBy;
    private Instant generatedAt;
    private Map<String, Long> summary;
}