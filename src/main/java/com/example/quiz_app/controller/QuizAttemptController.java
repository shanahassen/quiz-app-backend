package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.*;
import com.example.quiz_app.dto.AnswerSubmissionResponse;
import com.example.quiz_app.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quiz-attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService attemptService;

    @PostMapping
    public ResponseEntity<ApiResponse> startAttempt(
            @RequestBody(required = false) StartQuizAttemptRequest request,
            Authentication authentication
    ) throws QuizException {

        QuizAttemptResponse attempt = attemptService.startAttempt(
                authentication.getName(), request);

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz attempt started successfully.")
                .withPayload(attempt);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{attemptRefId}/answers")
    public ResponseEntity<ApiResponse> submitAnswer(
            @PathVariable String attemptRefId,
            @RequestBody(required = false) SubmitAnswerRequest request,
            Authentication authentication
    ) throws QuizException {

        AnswerSubmissionResponse result = attemptService.submitAnswer(
                authentication.getName(), attemptRefId, request);

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Answer submitted successfully.")
                .withPayload(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attemptRefId}/submit")
    public ResponseEntity<ApiResponse> submitAttempt(
            @PathVariable String attemptRefId,
            Authentication authentication
    ) throws QuizException {

        QuizAttemptResponse result = attemptService.submitAttempt(
                authentication.getName(), attemptRefId);

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz submitted successfully.")
                .withPayload(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attemptRefId}")
    public ResponseEntity<ApiResponse> getAttempt(
            @PathVariable String attemptRefId,
            Authentication authentication
    ) throws QuizException {

        QuizAttemptResponse attempt = attemptService.getAttempt(
                authentication.getName(), attemptRefId);

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz attempt retrieved successfully.")
                .withPayload(attempt);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getAttemptHistory(
            Authentication authentication
    ) throws QuizException {

        List<QuizAttemptHistoryResponse> history =
                attemptService.getAttemptHistory(authentication.getName());

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz history retrieved successfully.")
                .withPayload(history);

        return ResponseEntity.ok(response);
    }
}