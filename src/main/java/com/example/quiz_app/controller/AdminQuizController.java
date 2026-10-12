package com.example.quiz_app.quiz.controller;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.quiz.dto.QuizAdminResponse;
import com.example.quiz_app.quiz.dto.QuizRequest;
import com.example.quiz_app.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminQuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<List<QuizAdminResponse>> getAll() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizAdminResponse> getById(@PathVariable Long id) throws QuizException {
        return ResponseEntity.ok(quizService.getQuizForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<QuizAdminResponse> create(@Valid @RequestBody QuizRequest request)
            throws QuizException {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizAdminResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody QuizRequest request)
            throws QuizException {
        return ResponseEntity.ok(quizService.updateQuiz(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws QuizException {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}