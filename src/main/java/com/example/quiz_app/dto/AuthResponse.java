package com.example.quiz_app.dto;

public record AuthResponse(
        String token,
        String userId,
        String firstName,
        String lastName,
        String email,
        String role) {
}
