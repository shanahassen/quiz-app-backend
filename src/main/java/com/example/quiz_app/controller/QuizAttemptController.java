package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.AnswerFeedbackResponse;
import com.example.quiz_app.dto.QuizAttemptHistoryResponse;
import com.example.quiz_app.dto.QuizAttemptResponse;
import com.example.quiz_app.dto.StartQuizAttemptRequest;
import com.example.quiz_app.dto.SubmitAnswerRequest;
import com.example.quiz_app.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
<<<<<<< Updated upstream
=======

import java.util.List;
>>>>>>> Stashed changes

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
                authentication.getName(),
                request
        );

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

        AnswerFeedbackResponse feedback = attemptService.submitAnswer(
                authentication.getName(),
                attemptRefId,
                request
        );

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Answer submitted successfully.")
                .withPayload(feedback);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attemptRefId}/submit")
    public ResponseEntity<ApiResponse> submitAttempt(
            @PathVariable String attemptRefId,
            Authentication authentication
    ) throws QuizException {

        QuizAttemptResponse result = attemptService.submitAttempt(
                authentication.getName(),
                attemptRefId
        );

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz submitted successfully.")
                .withPayload(result);

        return ResponseEntity.ok(response);
    }

    /**
     * Marks the current participant's attempt as INCOMPLETE.
     * This does not submit, score, or generate a certificate.
     */
    @PostMapping("/{attemptRefId}/exit")
    public ResponseEntity<ApiResponse> exitAttempt(
            @PathVariable String attemptRefId,
            Authentication authentication
    ) throws QuizException {

        QuizAttemptResponse result = attemptService.exitAttempt(
                authentication.getName(),
                attemptRefId
        );

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz attempt exited and saved as incomplete.")
                .withPayload(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attemptRefId}")
    public ResponseEntity<ApiResponse> getAttempt(
            @PathVariable String attemptRefId,
            Authentication authentication
    ) throws QuizException {

        QuizAttemptResponse attempt = attemptService.getAttempt(
                authentication.getName(),
                attemptRefId
        );

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz attempt retrieved successfully.")
                .withPayload(attempt);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getAttemptHistory(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable
    ) throws QuizException {

<<<<<<< Updated upstream
        Page<QuizAttemptHistoryResponse> history =
                attemptService.getAttemptHistory(authentication.getName(), pageable);
=======
        List<QuizAttemptHistoryResponse> history =
                attemptService.getAttemptHistory(
                        authentication.getName()
                );
>>>>>>> Stashed changes

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Quiz history retrieved successfully.")
                .withPayload(history.getContent(), history.getTotalElements());

        return ResponseEntity.ok(response);
    }
}