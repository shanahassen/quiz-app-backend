package com.example.quiz_app.dto;

public record CreatePermissionRequest(
        String name,
        String description
) {
}