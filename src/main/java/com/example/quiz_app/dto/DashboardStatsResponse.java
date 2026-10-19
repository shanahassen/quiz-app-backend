package com.example.quiz_app.dto;

public record DashboardStatsResponse(
        long activeUsers,
        long activeCategories,
        long draftQuizzes,
        long publishedQuizzes,
        long archivedQuizzes
) {
}