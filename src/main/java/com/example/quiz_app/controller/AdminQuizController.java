package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.QuizRequest;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class AdminQuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<ApiResponse> createQuiz(
            @RequestBody(required = false) QuizRequest request
    ) throws QuizException {
        Quiz quiz = quizService.createQuiz(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quiz created successfully")
                        .withPayload(quiz)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quizzes retrieved successfully")
                        .withPayload(quizzes, quizzes.size())
        );
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse> getQuiz(
            @PathVariable String refId
    ) throws QuizException {
        Quiz quiz = quizService.getQuizByRefId(refId);

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quiz retrieved successfully")
                        .withPayload(quiz)
        );
    }

    @PutMapping("/{refId}")
    public ResponseEntity<ApiResponse> updateQuiz(
            @PathVariable String refId,
            @RequestBody(required = false) QuizRequest request
    ) throws QuizException {
        Quiz quiz = quizService.updateQuiz(refId, request);

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quiz updated successfully")
                        .withPayload(quiz)
        );
    }

    @PatchMapping("/{refId}/publish")
    public ResponseEntity<ApiResponse> publishQuiz(
            @PathVariable String refId
    ) throws QuizException {
        Quiz quiz = quizService.publishQuiz(refId);

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quiz published successfully")
                        .withPayload(quiz)
        );
    }

    @PatchMapping("/{refId}/draft")
    public ResponseEntity<ApiResponse> moveQuizToDraft(
            @PathVariable String refId
    ) throws QuizException {
        Quiz quiz = quizService.moveQuizToDraft(refId);

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quiz returned to draft successfully")
                        .withPayload(quiz)
        );
    }

    @PatchMapping("/{refId}/archive")
    public ResponseEntity<ApiResponse> archiveQuiz(
            @PathVariable String refId
    ) throws QuizException {
        Quiz quiz = quizService.archiveQuiz(refId);

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Quiz archived successfully")
                        .withPayload(quiz)
        );
    }
}