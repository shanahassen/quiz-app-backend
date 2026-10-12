package com.example.quiz_app.quiz.controller;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.quiz.dto.QuizUserResponse;
import com.example.quiz_app.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only endpoints for logged-in users (and admins previewing the user view).
 * Correct answers are never returned here.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
@RequiredArgsConstructor
public class UserQuizController {

    private final QuizService quizService;

    @GetMapping("/categories/{categoryId}/quizzes")
    public ResponseEntity<List<QuizUserResponse>> getByCategory(@PathVariable Long categoryId)
            throws QuizException {
        return ResponseEntity.ok(quizService.getQuizzesByCategory(categoryId));
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<QuizUserResponse> getById(@PathVariable Long id) throws QuizException {
        return ResponseEntity.ok(quizService.getQuizForUser(id));
    }
}