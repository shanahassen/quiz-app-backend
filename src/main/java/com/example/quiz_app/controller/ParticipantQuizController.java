package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.ParticipantQuizResponse;
import com.example.quiz_app.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/participant/quizzes")
@RequiredArgsConstructor
public class ParticipantQuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPublishedQuizzes() {
        List<ParticipantQuizResponse> quizzes =
                quizService.getPublishedQuizzes();

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Published quizzes retrieved successfully.")
                .withPayload(quizzes, quizzes.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse> getPublishedQuiz(
            @PathVariable String refId) throws QuizException {

        ParticipantQuizResponse quiz =
                quizService.getPublishedQuizByRefId(refId);

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Published quiz retrieved successfully.")
                .withPayload(quiz);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizRefId}/questions/{questionRefId}/hint")
    public ResponseEntity<ApiResponse> getQuestionHint(
            @PathVariable String quizRefId,
            @PathVariable String questionRefId) throws QuizException {

        String hint = quizService.getQuestionHint(
                quizRefId, questionRefId);

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Question hint retrieved successfully.")
                .withPayload(hint);

        return ResponseEntity.ok(response);
    }
}