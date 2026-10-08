package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

    @Id
    private String refId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
