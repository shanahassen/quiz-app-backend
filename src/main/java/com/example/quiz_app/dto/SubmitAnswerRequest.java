package com.example.quiz_app.dto;

public record SubmitAnswerRequest(
    String questionRefId,
    String selectedOptionRefId
) {}
